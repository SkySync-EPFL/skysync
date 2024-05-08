package ch.epfl.skysync.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.SnackbarManager
import ch.epfl.skysync.models.location.Location
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

  // Temporary
  private val userTable = repository.userTable

  private val locationTable = repository.locationTable
  private val listeners = mutableListOf<ListenerRegistration>()

  // Flow to observe location updates
  private val _locations = MutableStateFlow<List<Location>>(emptyList())
  val locations: StateFlow<List<Location>> = _locations.asStateFlow()

  /**
   * Fetches the location of a list of user IDs and listens for updates.
   *
   * @param userIds List of user IDs whose locations are to be fetched and observed.
   */
  private fun addLocationListeners(userIds: List<String>) {
    listeners +=
        userIds.map { userId ->
          locationTable.listenForLocationUpdates(
              userId,
              { update ->
                var locations = _locations.value

                // remove current locations that will be updated/added
                locations =
                    locations.filter { location ->
                      update.adds.find { it.id == location.id } == null &&
                          update.updates.find { it.id == location.id } == null
                    }
                // add new locations
                _locations.value = locations + update.adds + update.updates
              },
              viewModelScope)
        }
  }

  /**
   * Updates the location of the current user.
   *
   * @param location The new location to update in the database.
   */
  fun updateLocation(location: Location) =
      viewModelScope.launch { locationTable.updateLocation(location, onError = { onError(it) }) }

  /** Callback executed when an error occurs on database-related operations */
  private fun onError(e: Exception) {
    SnackbarManager.showMessage(e.message ?: "An unknown error occurred")
  }

  init {
    // Temporary to show it works, will later be connected to a "start/stop flight" button for ex
    // Will need a shared ViewModel later
    viewModelScope.launch {
      val userIds = userTable.getAll().map { it.id }
      addLocationListeners(userIds)
    }
  }

  override fun onCleared() {
    listeners.forEach { it.remove() }
    listeners.clear()
    super.onCleared()
  }
}
