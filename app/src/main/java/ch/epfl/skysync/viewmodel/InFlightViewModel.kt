package ch.epfl.skysync.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.SnackbarManager
import ch.epfl.skysync.database.DateUtility
import ch.epfl.skysync.database.ListenerUpdate
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.location.FlightTrace
import ch.epfl.skysync.models.location.Location
import ch.epfl.skysync.models.user.User
import ch.epfl.skysync.util.WhileUiSubscribed
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ListenerRegistration
import java.time.LocalDate
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** ViewModel for the location tracking of the user during a flight and the timer. */
class InFlightViewModel(val repository: Repository) : ViewModel() {

  companion object {
    @Composable
    fun createViewModel(repository: Repository): InFlightViewModel {
      return viewModel<InFlightViewModel>(
          factory =
              object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                  return InFlightViewModel(repository) as T
                }
              })
    }
  }

  private val locationTable = repository.locationTable
  private val flightTraceTable = repository.flightTraceTable

  /** If the view model has been initialized */
  private var initialized = false
  /**
   * The user ID, initialized in the graph (and not at creation in MainActivity as we don't have it
   * then)
   */
  private lateinit var userId: String
  /** List of listeners listening to the location of users in the ongoing flight */
  private val locationListeners = mutableListOf<ListenerRegistration>()
  /** Map User ID -> User of the users in the ongoing flight */
  private val users = mutableMapOf<String, User>()
  /** ID of the pilot of the ongoing flight */
  private var pilotId: String? = null

  /** The job that runs the timer */
  private var timerJob: Job? = null
  /** The last timestamp taken by the timer job */
  private var lastTimestamp = 0L

  private val _counter = MutableStateFlow(0L)
  /** The current counter value in milliseconds.* */
  val rawCounter = _counter.asStateFlow()
  /** The current value of the counter formatted as a string in the format "HH:MM:SS". */
  val counter =
      _counter
          .map { DateUtility.formatTime(it) }
          .stateIn(
              viewModelScope, started = WhileUiSubscribed, initialValue = DateUtility.formatTime(0))

  private val _inFlight = MutableStateFlow<Boolean>(false)
  /** If there is an ongoing flight */
  val inFlight: StateFlow<Boolean> = _inFlight.asStateFlow()

  private val _currentLocations = MutableStateFlow<Map<String, Pair<User, Location>>>(emptyMap())
  /** Map User ID -> (User, Location) of the current location of each user in the ongoing flight */
  val currentLocations: StateFlow<Map<String, Pair<User, Location>>> =
      _currentLocations.asStateFlow()

  private val _flightLocations = MutableStateFlow<List<Location>>(emptyList())
  /** List of the successive locations through which the balloon (pilot) has passed */
  val flightLocations: StateFlow<List<Location>> = _flightLocations.asStateFlow()

  private val _confirmedFlights: MutableStateFlow<List<ConfirmedFlight>?> = MutableStateFlow(null)
  /** List of confirmed flights scheduled today the user is assigned to */
  val confirmedFlights = _confirmedFlights.asStateFlow()

  private val _flight = MutableStateFlow<ConfirmedFlight?>(null)
  /** The ID of the ongoing flight */
  val flight = _flight.asStateFlow()

  /**
   * Initialize the view model.
   *
   * This method can be called multiple times, it will only be executed once
   */
  fun init(userId: String) {
    if (!initialized) {
      initialized = true
      this.userId = userId
      refreshFlights()
    }
  }

  /** Fetch the flights the user is assigned to */
  fun refreshFlights() =
      viewModelScope.launch {
        val flights =
            repository.userTable.retrieveAssignedFlights(
                repository.flightTable, userId, onError = { onError(it) })
        // only keep confirmed flight of today
        // to not check for ongoing flight here, will be done in listener
        _confirmedFlights.value =
            flights.filterIsInstance<ConfirmedFlight>().filter { flight ->
              flight.date.isEqual(LocalDate.now())
            }
        Log.d("InFlightViewModel", "Personal Flights are loaded")
      }

  /**
   * Starts the timer
   *
   * Reset the counter, start a new coroutine. Uses timestamp to update the counter every approx.
   * 100ms.
   */
  private fun startTimer() {
    _counter.value = 0L
    var newTimeStamp = 0L
    timerJob =
        viewModelScope.launch {
          lastTimestamp = System.currentTimeMillis()
          while (_inFlight.value) {
            delay(100)
            newTimeStamp = System.currentTimeMillis()
            _counter.value += newTimeStamp - lastTimestamp
            lastTimestamp = newTimeStamp
          }
        }
  }

  /**
   * Start a flight
   *
   * Starts the timer and location tracking. This will fail if there is already a flight ongoing or
   * if the flight is not found.
   *
   * @param flightId The ID of the flight to start
   */
  fun startFlight(flightId: String) {
    if (_inFlight.value) {
      onError(Exception("There is already a flight ongoing."))
      return
    }
    val flight = _confirmedFlights.value?.find { it.id == flightId }
    if (flight == null) {
      onError(Exception("Flight not found."))
      return
    }
    _inFlight.value = true
    _flight.value = flight
    startLocationTracking(flight.team)
    startTimer()
  }

  /**
   * Stop the ongoing flight
   *
   * Stop the timer and location tracking.
   *
   * If the user is the pilot, save the finished flight and flight trace.
   */
  fun stopFlight() {
    if (!_inFlight.value) return
    _inFlight.value = false
    stopTimer()
    stopLocationTracking()

    if (userId == pilotId) {
      saveFinishedFlight()
      saveFlightTrace()
    }
  }

  /** Save the finished flight to the database */
  private fun saveFinishedFlight() {
    // TODO: save the finished flight to the database
    Log.d("LocationViewModel", "Saving finished flight")
  }

  /**
   * Stops the timer
   *
   * Cancel the timer coroutine. The counter is not reset.
   */
  private fun stopTimer() {
    timerJob?.cancel()
    timerJob = null
    lastTimestamp = 0L
  }

  /**
   * Fetches the location of a list of user IDs and listens for updates.
   *
   * @param userIds List of user IDs whose locations are to be fetched and observed.
   * @param pilotId The ID of the pilot that is used to construct the trace of the flight
   */
  private fun addLocationListeners(userIds: List<String>, pilotId: String) {
    locationListeners +=
        userIds.map { userId ->
          locationTable.listenForLocationUpdates(
              userId,
              { update ->
                if (userId == pilotId) {
                  updateFlightLocations(update)
                }
                if (update.adds.isEmpty()) {
                  return@listenForLocationUpdates
                }
                // the return is useless but needed to make sonar cloud happy
                val user = users[userId] ?: return@listenForLocationUpdates

                val lastLocation = update.adds.last()
                _currentLocations.value =
                    _currentLocations.value.plus(Pair(userId, Pair(user, lastLocation)))
              },
              viewModelScope)
        }
  }

  /** Update the flight trace locations according to the received update */
  private fun updateFlightLocations(update: ListenerUpdate<Location>) {
    var locations = _flightLocations.value
    // do not take deletions into account, as it is just deletions from the query results
    // that is: it is not the location with the highest time anymore
    val updatedLocations = update.adds + update.updates
    locations =
        locations.filter { location -> updatedLocations.find { it.id == location.id } == null }
    // add new locations
    _flightLocations.value = (locations + update.adds + update.updates).sortedBy { it.point.time }
  }

  /**
   * Updates the location of the current user.
   *
   * @param location The new location to update in the database.
   */
  fun addLocation(location: Location) =
      viewModelScope.launch { locationTable.addLocation(location, onError = { onError(it) }) }

  /**
   * Save the flight trace to the flight trace table.
   *
   * Note: The flight trace is composed of the locations of the pilot, but this function can be
   * called from any member of the flight, thus responsibility of the call of this function needs to
   * be clearly defined.
   */
  fun saveFlightTrace() =
      viewModelScope.launch {
        if (!_inFlight.value || pilotId == null) {
          onError(Exception("Can not save the flight trace while not in flight."))
          return@launch
        }
        // first verify that the number of locations in local and in the database match
        // as the pilot could already have deleted his locations
        val locations =
            locationTable.query(Filter.equalTo("userId", pilotId!!), onError = { onError(it) })
        if (_flightLocations.value.size != locations.size) {
          onError(Exception("Can not save the flight trace (consistency issue)."))
          return@launch
        }
        val flightTrace = FlightTrace(trace = _flightLocations.value.map { it.point })
        flightTraceTable.set(_flight.value!!.id, flightTrace, onError = { onError(it) })
      }

  /**
   * Start location tracking
   *
   * Setup listeners on flight members' to track their current location and start to construct
   * flight trace.
   */
  fun startLocationTracking(team: Team) {
    val users = team.roles.map { it.assignedUser!! }
    users.forEach { this.users[it.id] = it }

    val userIds = users.map { it.id }
    pilotId = team.roles.find { it.roleType == RoleType.PILOT }?.assignedUser?.id
    if (pilotId == null) {
      onError(Exception("Missing a pilot on flight."))
      return
    }
    addLocationListeners(userIds, pilotId!!)
  }

  /** Stop the flight tracking, clean listeners and delete all locations of the user */
  fun stopLocationTracking() =
      viewModelScope.launch {
        pilotId = null
        users.clear()
        locationListeners.forEach { it.remove() }
        locationListeners.clear()
        _currentLocations.value = emptyMap()
        _flightLocations.value = emptyList()
        locationTable.queryDelete(Filter.equalTo("userId", userId), onError = { onError(it) })
      }

  /** Callback executed when an error occurs on database-related operations */
  private fun onError(e: Exception) {
    SnackbarManager.showMessage(e.message ?: "An unknown error occurred")
  }

  override fun onCleared() {
    stopLocationTracking()
    stopTimer()
    super.onCleared()
  }
}
