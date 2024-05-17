package ch.epfl.skysync.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.SnackbarManager
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.user.Admin
import ch.epfl.skysync.models.user.Crew
import ch.epfl.skysync.models.user.Pilot
import ch.epfl.skysync.models.user.User
import ch.epfl.skysync.util.WhileUiSubscribed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** ViewModel for the user for the finished flights */
class FinishedFlightsViewModel(val repository: Repository, val userId: String?) : ViewModel() {
  companion object {
    @Composable
    fun createViewModel(
        repository: Repository,
        userId: String?,
    ): FinishedFlightsViewModel {
      return viewModel<FinishedFlightsViewModel>(
          factory =
              object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                  return FinishedFlightsViewModel(repository, userId) as T
                }
              })
    }
  }

  private val _currentFlights: MutableStateFlow<List<FinishedFlight>?> = MutableStateFlow(null)
  private val _currentUser: MutableStateFlow<User?> = MutableStateFlow(null)
  private val _selectedFlightId: MutableStateFlow<String?> = MutableStateFlow(null)
  private val isLoading = MutableStateFlow(false)

  val currentFlights = _currentFlights.asStateFlow()
  val currentUser = _currentUser.asStateFlow()

  fun refresh() {
    refreshUserAndFlights()
  }

  private fun refreshUser() =
      viewModelScope.launch {
        _currentUser.value = repository.userTable.get(userId ?: UNSET_ID, onError = { onError(it) })
      }

  private fun refreshFlights() =
      viewModelScope.launch {
        if (_currentUser.value is Admin) {
          _currentFlights.value =
              repository.flightTable
                  .getAll(onError = { onError(it) })
                  .filterIsInstance<FinishedFlight>()
        } else if (_currentUser.value is Pilot || _currentUser.value is Crew) {
          _currentFlights.value =
              repository.userTable
                  .retrieveAssignedFlights(
                      repository.flightTable, userId ?: UNSET_ID, onError = { onError(it) })
                  .filterIsInstance<FinishedFlight>()
        }
      }

  /** Refreshes the user logged in and its finished flights */
  fun refreshUserAndFlights() =
      viewModelScope.launch {
        isLoading.value = true
        refreshUser().join()
        refreshFlights().join()
        isLoading.value = false
      }

  fun addFlight(flight: FinishedFlight) =
      viewModelScope.launch {
        repository.flightTable.add(flight, onError = { onError(it) })
        refreshUserAndFlights()
      }

  fun deleteFlight(flightId: String) =
      viewModelScope.launch {
        repository.flightTable.delete(flightId, onError = { onError(it) })
        refreshUserAndFlights()
      }

  fun getFlight(flightId: String): StateFlow<FinishedFlight?> {
    return _currentFlights
        .map { flights -> flights?.find { it.id == flightId } }
        .stateIn(scope = viewModelScope, started = WhileUiSubscribed, initialValue = null)
  }

  /*fun addReport(report: Report) =
  viewModelScope.launch {
    repository.reportTable.add(report, onError = { onError(it) })
  }
  fun addReport(reportId: String) =
  viewModelScope.launch {
    repository.reportTable.delete(report, onError = { onError(it) })
  }
  */

  private fun selectFlight(id: String) = viewModelScope.launch { _selectedFlightId.value = id }

  fun refreshAndSelectFlight(id: String) =
      viewModelScope.launch {
        refreshUserAndFlights().join()
        selectFlight(id)
      }

  /** Might be used in the future to get the flights by location with a search bar */
  fun getFlightByLocation(location: String): List<FinishedFlight>? {
    return _currentFlights.value?.filter {
      it.landingLocation.name == location ||
          it.takeOffLocation.name == location ||
          it.takeOffLocation.latlng().toString() == location ||
          it.landingLocation.latlng().toString() == location
    }
  }
  /** Callback executed when an error occurs on database-related operations */
  private fun onError(e: Exception) {
    SnackbarManager.showMessage(e.message ?: "An unknown error occurred")
  }
}
