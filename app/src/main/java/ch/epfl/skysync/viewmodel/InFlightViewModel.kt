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
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.flight.Flight
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

  /**
   * Represents the different stages of a flight
   * - [startFlight]: [IDLE] -> [ONGOING]
   * - [stopFlight]: [ONGOING] -> [POST]
   * - [clearFlight]: [POST] -> [IDLE]
   */
  enum class FlightStage {
    /** There is no active flight */
    IDLE,

    /** There is an ongoing flight */
    ONGOING,

    /** There a flight that is finished but still displayed */
    POST,
  }

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
  private val flightTable = repository.flightTable
  private val flightTraceTable = repository.flightTraceTable

  /** If the view model has been initialized */
  private var initialized = false

  /**
   * The user ID, initialized in the graph (and not at creation in MainActivity as we don't have it
   * then)
   */
  private var _userId: String? = null

  /** List of listeners listening to the location of users in the ongoing flight */
  private val locationListeners = mutableListOf<ListenerRegistration>()

  /** List of listeners listening to confirmed flights scheduled today where the user is assigned */
  private val flightListeners = mutableListOf<ListenerRegistration>()

  /** Map User ID -> User of the users in the ongoing flight */
  private val users = mutableMapOf<String, User>()

  /** ID of the pilot of the current flight */
  private var pilotId: String? = null

  /** The job that runs the timer */
  private var timerJob: Job? = null

  /** The timestamp of the start of the flight */
  private var startTimestamp = 0L

  private val _loading = MutableStateFlow(true)

  /** If the view model is loading something */
  val loading = _loading.asStateFlow()

  private val _counter = MutableStateFlow(0L)

  /** The current counter value in milliseconds.* */
  val rawCounter = _counter.asStateFlow()

  /** The current value of the counter formatted as a string in the format "HH:MM:SS". */
  val counter =
      _counter
          .map { DateUtility.formatTime(it) }
          .stateIn(
              viewModelScope, started = WhileUiSubscribed, initialValue = DateUtility.formatTime(0))

  private val _flightStage = MutableStateFlow(FlightStage.IDLE)

  /** The current flight stage */
  val flightStage: StateFlow<FlightStage> = _flightStage.asStateFlow()

  private val _currentLocations = MutableStateFlow<Map<String, Pair<User, Location>>>(emptyMap())

  /** Map User ID -> (User, Location) of the current location of each user in the ongoing flight */
  val currentLocations: StateFlow<Map<String, Pair<User, Location>>> =
      _currentLocations.asStateFlow()

  private val _flightLocations = MutableStateFlow<List<Location>>(emptyList())

  /** List of the successive locations through which the balloon (pilot) has passed */
  val flightLocations: StateFlow<List<Location>> = _flightLocations.asStateFlow()

  /** List of confirmed flights scheduled today where the user is assigned */
  private val _confirmedFlights: MutableStateFlow<List<ConfirmedFlight>> =
      MutableStateFlow(emptyList())

  val startableFlights: StateFlow<List<ConfirmedFlight>> =
      _confirmedFlights
          .map { flights -> flights.filter { flight -> isUserPilotRole(flight.team) } }
          .stateIn(scope = viewModelScope, started = WhileUiSubscribed, initialValue = emptyList())

  private val _currentFlight = MutableStateFlow<ConfirmedFlight?>(null)

  /** The ID of the current flight (might not yet be ongoing) */
  val currentFlight: StateFlow<ConfirmedFlight?> = _currentFlight.asStateFlow()

  /**
   * Initialize the view model.
   *
   * This method can be called multiple times, it will only be executed once.
   *
   * Note: this method is written as a coroutine job for testing purposes.
   */
  fun init(userId: String) =
      viewModelScope.launch {
        if (!initialized) {
          initialized = true
          _userId = userId
          refreshFlights()
          addFlightListeners()
        }
      }

  /** If the current flight stage is [FlightStage.ONGOING] */
  fun isOngoingFlight(): Boolean = _flightStage.value == FlightStage.ONGOING

  /** If the current flight stage is [FlightStage.POST] */
  fun isPostFlight(): Boolean = _flightStage.value == FlightStage.POST

  /** Returns if the user is the pilot of the ongoing flight */
  fun isPilot(): Boolean = pilotId == _userId

  /** Returns if the user is assigned a pilot role in the team */
  private fun isUserPilotRole(team: Team): Boolean {
    return team.roles.find { role ->
      role.roleType == RoleType.PILOT && role.assignedUser?.id == _userId
    } != null
  }

  /** Fetch the flights the user is assigned to */
  private suspend fun refreshFlights() {
    _loading.value = true
    val flights =
        repository.userTable.retrieveAssignedFlights(
            repository.flightTable, _userId!!, onError = { onError(it) })
    // only keep confirmed flight of today
    // to not check for ongoing flight here, will be done in listener
    _confirmedFlights.value =
        flights.filterIsInstance<ConfirmedFlight>().filter { flight ->
          flight.date.isEqual(LocalDate.now())
        }

    Log.d("InFlightViewModel", "Personal Flights are loaded")
    _loading.value = false
  }

  /** Add listeners to the previously fetched confirmed flights where the user is not the pilot. */
  private fun addFlightListeners() {
    // do not add listener to flight where the user is the pilot
    // as by construction only the pilot can start/stop the flight
    flightListeners +=
        _confirmedFlights.value
            .filter { flight -> !isUserPilotRole(flight.team) }
            .map { flight ->
              flightTable.addFlightListener(
                  flight.id,
                  { onFlightListenerUpdate(it) },
                  viewModelScope,
                  onError = { onError(it) })
            }
  }

  /** The function executed on flight listener update, start/stop a flight if needed. */
  private fun onFlightListenerUpdate(update: ListenerUpdate<Flight>) {
    val flights = update.adds + update.updates
    // The listener is specific to the flight, so we expect to have exactly one flight in the update
    if (flights.size != 1) return
    val flight = flights.last()

    if (flight is FinishedFlight || (flight is ConfirmedFlight && !flight.isOngoing)) {
      if (isOngoingFlight() && flight.id == _currentFlight.value!!.id) {
        stopFlight()
      }
    }
    if (flight is ConfirmedFlight && flight.isOngoing) {
      if (!isOngoingFlight()) {
        startTimestamp = flight.startTimestamp!!
        _currentFlight.value = flight
        startFlightInternal()
      }
    }
  }

  /**
   * Set the current flight
   *
   * This will fail with an error if the flight is not found.
   */
  fun setCurrentFlight(flightId: String) {
    if (_flightStage.value != FlightStage.IDLE) return
    val flight = _confirmedFlights.value.find { it.id == flightId }
    if (flight == null) {
      onError(Exception("Flight not found."))
      return
    }
    pilotId = flight.team.roles.find { it.roleType == RoleType.PILOT }?.assignedUser?.id
    if (pilotId == null) {
      onError(Exception("Missing a pilot on flight."))
      return
    }
    _currentFlight.value = flight
  }

  /**
   * Starts the timer
   *
   * Reset the counter, start a new coroutine. Uses timestamp to update the counter every approx.
   * 100ms.
   */
  private fun startTimer() {
    timerJob =
        viewModelScope.launch {
          while (isOngoingFlight()) {
            delay(100)
            _counter.value = System.currentTimeMillis() - startTimestamp
          }
        }
  }

  /**
   * Update the confirmed flight on the database to set [ConfirmedFlight.isOngoing] to true and set
   * the [ConfirmedFlight.startTimestamp]. This will notify other flight member that a flight as
   * started (through the listener).
   */
  private suspend fun startFlightUpdateDatabase() {
    val flight = _currentFlight.value!!.copy(isOngoing = true, startTimestamp = startTimestamp)
    flightTable.update(flight.id, flight, onError = { onError(it) })
    _currentFlight.value = flight
  }

  /**
   * Update the confirmed flight on the database to set [ConfirmedFlight.isOngoing] to false and set
   * the [ConfirmedFlight.startTimestamp] to null. This will notify other flight member that the
   * flight as stopped (through the listener).
   */
  private suspend fun stopFlightUpdateDatabase() {
    val flight = _currentFlight.value!!.copy(isOngoing = false, startTimestamp = null)
    flightTable.update(flight.id, flight, onError = { onError(it) })
    _currentFlight.value = flight
  }

  /**
   * Set in-flight, starts the timer and location tracking.
   *
   * Assumes the necessary assumptions (i.e. do not check anything).
   */
  private fun startFlightInternal() {
    _flightStage.value = FlightStage.ONGOING
    startLocationTracking(_currentFlight.value!!.team)
    startTimer()
  }

  /**
   * Start a flight
   *
   * Update the flight on the database to mark it as ongoing. Starts the timer and location
   * tracking. This will fail with an error if there is already a flight ongoing or if the flight is
   * not found.
   *
   * Note: this method should only be called by the pilot.
   */
  fun startFlight() =
      viewModelScope.launch {
        if (isOngoingFlight()) {
          onError(Exception("There is already a flight ongoing."))
          return@launch
        }
        if (_currentFlight.value == null) {
          onError(Exception("Missing the flight to start."))
          return@launch
        }
        // get the timestamp of the start of the flight
        // this is done here and not in startTimer as
        // it should only be done once per flight (by the pilot)
        startTimestamp = System.currentTimeMillis()

        startFlightUpdateDatabase()
        startFlightInternal()
      }

  /**
   * Stop the ongoing flight
   *
   * Stop the timer and location tracking.
   *
   * If the user is the pilot, save the finished flight and flight trace.
   *
   * Note: this method can (and should) be called by anyone
   */
  fun stopFlight() =
      viewModelScope.launch {
        if (!isOngoingFlight()) return@launch
        _flightStage.value = FlightStage.POST
        stopTimer()
        stopLocationTracking()

        if (_userId == pilotId) {
          stopFlightUpdateDatabase()
          saveFinishedFlight()
          saveFlightTrace()
        }
      }

  /**
   * Clear the flight data
   *
   * Clear the timer and location tracking data
   */
  fun clearFlight() =
      viewModelScope.launch {
        if (!isPostFlight()) return@launch
        _flightStage.value = FlightStage.IDLE
        pilotId = null
        _currentFlight.value = null

        clearTimer()
        clearLocationTracking()
      }

  /** Save the finished flight to the database */
  private suspend fun saveFinishedFlight() {
    // TODO: save the finished flight to the database
    Log.d("InFlightViewModel", "Saving finished flight")
  }

  /**
   * Stops the timer
   *
   * Cancel the timer coroutine. The counter is not reset.
   */
  private fun stopTimer() {
    timerJob?.cancel()
    timerJob = null
    startTimestamp = 0L
  }

  /** Reset the counter */
  private fun clearTimer() {
    _counter.value = 0L
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
    locations = locations.filter { location -> updatedLocations.none { it.id == location.id } }
    // add new locations
    _flightLocations.value = (locations + update.adds + update.updates).sortedBy { it.point.time }
  }

  /**
   * Updates the location of the current user.
   *
   * This is only done if there is an ongoing flight.
   *
   * @param location The new location to update in the database.
   */
  fun addLocation(location: Location) =
      viewModelScope.launch {
        if (!isOngoingFlight()) return@launch
        locationTable.addLocation(location, onError = { onError(it) })
      }

  /**
   * Save the flight trace to the flight trace table.
   *
   * Note: this method should only be called by the pilot.
   */
  private suspend fun saveFlightTrace() {
    // first verify that the number of locations in local and in the database match
    // as the pilot could already have deleted his locations
    val locations =
        locationTable.query(Filter.equalTo("userId", pilotId!!), onError = { onError(it) })
    if (_flightLocations.value.size != locations.size) {
      onError(Exception("Can not save the flight trace (consistency issue)."))
      return
    }
    val flightTrace = FlightTrace(trace = _flightLocations.value.map { it.point })
    flightTraceTable.set(_currentFlight.value!!.id, flightTrace, onError = { onError(it) })
  }

  /**
   * Start location tracking
   *
   * Setup listeners on flight members' to track their current location and start to construct
   * flight trace.
   */
  private fun startLocationTracking(team: Team) {
    val users = team.roles.map { it.assignedUser!! }
    users.forEach { this.users[it.id] = it }

    val userIds = users.map { it.id }
    addLocationListeners(userIds, pilotId!!)
  }

  /** Stop the flight tracking, clean listeners */
  private fun stopLocationTracking() {
    locationListeners.forEach { it.remove() }
    locationListeners.clear()
  }

  /**
   * Clear location tracking data (flight trace/current locations). Delete all locations of the
   * user.
   */
  private suspend fun clearLocationTracking() {
    users.clear()
    _currentLocations.value = emptyMap()
    _flightLocations.value = emptyList()
    locationTable.queryDelete(Filter.equalTo("userId", _userId), onError = { onError(it) })
  }

  /** Callback executed when an error occurs on database-related operations */
  private fun onError(e: Exception) {
    SnackbarManager.showMessage(e.message ?: "An unknown error occurred")
  }

  override fun onCleared() {
    stopLocationTracking()
    stopTimer()
    viewModelScope.launch { clearLocationTracking() }
    clearTimer()
    super.onCleared()
  }
}
