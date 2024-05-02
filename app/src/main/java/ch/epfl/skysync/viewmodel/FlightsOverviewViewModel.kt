package ch.epfl.skysync.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.Repository
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.user.Admin
import ch.epfl.skysync.models.user.Crew
import ch.epfl.skysync.models.user.Pilot
import ch.epfl.skysync.models.user.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** ViewModel for the user */
class FlightsOverviewViewModel(
    val repository: Repository,
    val userId: String?,
) : ViewModel() {
  companion object {
    @Composable
    fun createViewModel(
        repository: Repository,
        userId: String?,
    ): FlightsOverviewViewModel {
      return viewModel<FlightsOverviewViewModel>(
          factory =
              object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                  return FlightsOverviewViewModel(repository, userId) as T
                }
              })
    }
  }

  private val _currentFlights: MutableStateFlow<List<Flight>?> = MutableStateFlow(null)
  private val _currentUser = MutableStateFlow<User?>(null)

  val currentFlights = _currentFlights.asStateFlow()
  val currentUser = _currentUser.asStateFlow()

  fun refresh() {
    refreshUserAndFlights()
  }

  fun refreshUserAndFlights() =
      viewModelScope.launch {
        _currentUser.value = repository.userTable.get(userId ?: UNSET_ID, onError = { onError(it) })
        if (_currentUser.value is Admin) {
          Log.d("FlightsViewModel", "Admin user loaded")
          _currentFlights.value = repository.flightTable.getAll(onError = { onError(it) })
        } else if (_currentUser.value is Pilot || _currentUser.value is Crew) {
          _currentFlights.value =
              repository.userTable.retrieveAssignedFlights(
                  repository.flightTable, userId ?: UNSET_ID, onError = { onError(it) })
          Log.d("FlightsViewModel", "Pilot or Crew user loaded")
        }
      }

  /** Callback executed when an error occurs on database-related operations */
  private fun onError(e: Exception) {
    // TODO: display error message
  }

  init {
    refresh()
  }
}
