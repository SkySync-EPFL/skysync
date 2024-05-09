package ch.epfl.skysync.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.SnackbarManager
import ch.epfl.skysync.database.ListenerUpdate
import ch.epfl.skysync.models.calendar.getTimeSlot
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.location.FlightTrace
import ch.epfl.skysync.models.location.Location
import ch.epfl.skysync.models.user.User
import ch.epfl.skysync.util.WhileUiSubscribed
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class LocationViewModel(val userId: String, val repository: Repository) : ViewModel() {

  companion object {
    @Composable
    fun createViewModel(uid: String, repository: Repository): LocationViewModel {
      return viewModel<LocationViewModel>(
        factory =
        object : ViewModelProvider.Factory {
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LocationViewModel(uid, repository) as T
          }
        })
    }
  }

  fun refreshPersonalFlights() =
    viewModelScope.launch {
      refreshPersonalFlights2()
  }

  suspend fun refreshPersonalFlights2(){
    _currentUser.value = repository.userTable.get(userId, onError = { onError(it) })
    _personalFlights.value = repository
      .userTable
      .retrieveAssignedFlights(
        repository.flightTable,
        userId,
        onError = { onError(it) })
    Log.d("InFlightViewModel", "Personal Flights are loaded")
  }


  private var timerJob: Job? = null
  private var lastTimestamp = 0L
  private val _counter = MutableStateFlow(0L)

  val rawCounter = _counter.asStateFlow()

  /** The current value of the counter formatted as a string in the format "HH:MM:SS". */
  val counter =
    _counter
      .map { formatTime(it) }
      .stateIn(viewModelScope, started = WhileUiSubscribed, initialValue = formatTime(0))

  private val locationTable = repository.locationTable
  private val flightTraceTable = repository.flightTraceTable
  private val listeners = mutableListOf<ListenerRegistration>()
  private val users = mutableMapOf<String, User>()

  private var pilotId: String? = null
  private val _inFlight = MutableStateFlow<Boolean>(false)
  val inFlight: StateFlow<Boolean> = _inFlight.asStateFlow()

  private val _currentLocations = MutableStateFlow<Map<String, Pair<User, Location>>>(emptyMap())
  val currentLocations: StateFlow<Map<String, Pair<User, Location>>> =
      _currentLocations.asStateFlow()

  private val _flightLocations = MutableStateFlow<List<Location>>(emptyList())
  val flightLocations: StateFlow<List<Location>> = _flightLocations.asStateFlow()

  private val _personalFlights: MutableStateFlow<List<Flight>?> = MutableStateFlow(null)
  private var currentFlight: Flight? = null
  private val _currentUser = MutableStateFlow<User?>(null)


  private fun getCurrentFlight(flights: List<Flight>?): Flight?{
    if(flights == null) return null
    val currentTimeSlot = getTimeSlot(LocalTime.now())
    val currentDate = LocalDate.now()
    return flights.find {
      f -> f.date == currentDate && f.timeSlot == currentTimeSlot
    }
  }

  /**
   * Starts the timer on a reset counter in a new coroutine and sets isRunning to true. Uses
   * timestamp to update the counter every approx. 100ms.
   */
  fun startTimer() {
    if (_inFlight.value) return
    _counter.value = 0L
    var newTimeStamp = 0L
    timerJob = viewModelScope.launch {
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
   * starts the timer, position tracking and flightTrace
   */
  fun startFlight(){
    viewModelScope.launch {
      refreshPersonalFlights2()
      currentFlight = getCurrentFlight(_personalFlights.value)
        if(currentFlight != null){
          _inFlight.value = true
          startLocationTracking(currentFlight!!.team)
          startTimer()
        }
    }
  }

  /**
   * stops the timer and flight tracking
   */
  fun stopFlight(){
    _inFlight.value = false
    stopTimer()
    stopLocationTracking()
    saveFinishedFlight()
  }


  /**
   * saved the finished flight to the database
   */
  fun saveFinishedFlight(){
    //TODO: save the finished flight to the database
    Log.d("LocationViewModel", "Saving finished flight")
    }

  /** Formats the given time in milliseconds to a string in the format "HH:MM:SS". */
  private fun formatTime(milliseconds: Long): String {
    val secondsRounded = milliseconds / 1000
    val hours = secondsRounded / 3600
    val minutes = (secondsRounded % 3600) / 60
    val remainingSeconds = secondsRounded % 60
    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
  }

  /**
   * Stops the timer by setting isRunning to false and cancelling the coroutine that updates the
   * counter. The time is not reset.
   */
  fun stopTimer() {
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
    listeners +=
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

  /** Callback executed when an error occurs on database-related operations */
  private fun onError(e: Exception) {
    SnackbarManager.showMessage(e.message ?: "An unknown error occurred")
  }

  /** Reset the internal flight specific attributes */
  private fun reset() {
    users.clear()
    listeners.forEach { it.remove() }
    listeners.clear()
    _currentLocations.value = emptyMap()
    _flightLocations.value = emptyList()
  }

  /**
   * Save the flight trace to the flight trace table.
   *
   * Note: The flight trace is composed of the locations of the pilot, but this function can be
   * called from any member of the flight, thus responsibility of the call of this function needs to
   * be clearly defined.
   */
  fun saveFlightTrace() =
      viewModelScope.launch {
        if (!_inFlight.value || currentFlight?.id == null || pilotId == null) {
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
        flightTraceTable.set(currentFlight!!.id, flightTrace, onError = { onError(it) })
      }

  /**
   * Setup the view model to be in "flight-mode", setup listeners on flight members' to track their
   * current location and start to construct flight trace.
   */
  fun startLocationTracking(team: Team) {
    reset()
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

  /** Stop the flight tracking,  clean listeners and delete all locations of the user */
  fun stopLocationTracking() =
      viewModelScope.launch {
        reset()
        locationTable.queryDelete(Filter.equalTo("userId", userId), onError = { onError(it) })
      }




  override fun onCleared() {
    reset()
    stopTimer()
    super.onCleared()
  }
}
