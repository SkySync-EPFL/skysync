package ch.epfl.skysync.viewmodel

import android.util.Log
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FinishedFlightsViewModel(
    val repository: Repository,
    val userId: String?,
) : ViewModel() {
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
    val _selectedFlight : MutableStateFlow<FinishedFlight?> = MutableStateFlow(null)

    val currentFlights = _currentFlights.asStateFlow()
    val currentUser = _currentUser.asStateFlow()
    val selectedFlight = _selectedFlight.asStateFlow()


    fun refresh() {
        refreshUserAndFlights()
    }

    private fun refreshUserAndFlights() =
        viewModelScope.launch {
            _currentUser.value = repository.userTable.get(userId ?: UNSET_ID, onError = { onError(it) })
            if (_currentUser.value is Admin) {
                Log.d("FlightsViewModel", "Admin user loaded")
                _currentFlights.value = repository.flightTable.getAll(onError = { onError(it) }).filterIsInstance<FinishedFlight>()
            } else if (_currentUser.value is Pilot || _currentUser.value is Crew) {
                _currentFlights.value =
                    repository.userTable.retrieveAssignedFlights(
                        repository.flightTable, userId ?: UNSET_ID, onError = { onError(it) }).filterIsInstance<FinishedFlight>()
                Log.d("FlightsViewModel", "Pilot or Crew user loaded")
            }
        }

    fun selectFlight(id: String) {
        _selectedFlight.value = _currentFlights.value?.find{ it.id == id }
    }

    fun getFlightByLocation(location: String): List<FinishedFlight>? {
        return _currentFlights.value?.filter {
            it.landingLocation.name == location
                    || it.takeOffLocation.name == location
                    || it.takeOffLocation.latlng().toString() == location
                    || it.landingLocation.latlng().toString() == location
        }
    }





    /** Callback executed when an error occurs on database-related operations */
    private fun onError(e: Exception) {
        SnackbarManager.showMessage(e.message ?: "An unknown error occurred")
    }
}
