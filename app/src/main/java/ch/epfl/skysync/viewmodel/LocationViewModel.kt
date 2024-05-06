package ch.epfl.skysync.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.ListenerUpdate
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.location.Location
import ch.epfl.skysync.models.user.User
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationViewModel(repository: Repository) : ViewModel() {

  companion object {
    @Composable
    fun createViewModel(repository: Repository): LocationViewModel {
      return viewModel<LocationViewModel>(
          factory =
              object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                  return LocationViewModel(repository) as T
                }
              })
    }
  }

  private val locationTable = repository.locationTable
  private val listeners = mutableListOf<ListenerRegistration>()
  private val users = mutableMapOf<String, User>()

  private val _inFlight = MutableStateFlow<Boolean>(false)
  val inFlight: StateFlow<Boolean> = _inFlight.asStateFlow()

  private val _currentLocations = MutableStateFlow<Map<String, Pair<User, Location>>>(emptyMap())
  val currentLocations: StateFlow<Map<String, Pair<User, Location>>> =
      _currentLocations.asStateFlow()

  private val _flightLocations = MutableStateFlow<List<Location>>(emptyList())
  val flightLocations: StateFlow<List<Location>> = _flightLocations.asStateFlow()

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
                val user = users[userId]!!
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
    val updatedLocations = update.adds + update.updates + update.deletes
    locations =
        locations.filter { location -> updatedLocations.find { it.id == location.id } == null }
    // add new locations
    _flightLocations.value = (locations + update.adds + update.updates).sortedBy { it.data.time }
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
    // TODO: display error message
  }

  /** Reset the internal mutable attributes */
  private fun reset() {
    users.clear()
    listeners.forEach { it.remove() }
    listeners.clear()
  }

  /**
   * Setup the view model to be in "flight-mode", setup listeners on flight members' to track their
   * current location and start to construct flight trace.
   */
  fun startFlight(flight: ConfirmedFlight) {
    reset()

    val users = flight.team.roles.map { it.assignedUser!! }
    users.forEach { this.users[it.id] = it }

    val userIds = users.map { it.id }
    val pilotId = flight.team.roles.find { it.roleType == RoleType.PILOT }?.assignedUser!!.id
    if (pilotId == null) {
      onError(Exception("Missing a pilot on flight."))
      return
    }
    _inFlight.value = true

    addLocationListeners(userIds, pilotId)
  }

  /** Stop the flight, clean listeners */
  fun endFlight() {
    reset()
    _inFlight.value = false
  }

  override fun onCleared() {
    reset()
    super.onCleared()
  }
}
