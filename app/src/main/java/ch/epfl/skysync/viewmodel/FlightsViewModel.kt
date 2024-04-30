package ch.epfl.skysync.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.Repository
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Vehicle
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

/** ViewModel for the user */
class FlightsViewModel(
    val repository: Repository,
    val userId: String?,
) : ViewModel() {
  companion object {
    @Composable
    fun createViewModel(
        repository: Repository,
        userId: String?,
    ): FlightsViewModel {
      return viewModel<FlightsViewModel>(
          factory =
              object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                  return FlightsViewModel(repository, userId) as T
                }
              })
    }
  }

  private val _currentFlights: MutableStateFlow<List<Flight>?> = MutableStateFlow(null)
  private val _currentBalloons: MutableStateFlow<List<Balloon>> = MutableStateFlow(emptyList())
  private val _currentBaskets: MutableStateFlow<List<Basket>> = MutableStateFlow(emptyList())
  private val _currentFlightTypes: MutableStateFlow<List<FlightType>> =
      MutableStateFlow(emptyList())
  private val _currentVehicles: MutableStateFlow<List<Vehicle>> = MutableStateFlow(emptyList())
  private val _currentUser = MutableStateFlow<User?>(null)

  val currentFlights = _currentFlights.asStateFlow()
  val currentBalloons = _currentBalloons.asStateFlow()
  val currentBaskets = _currentBaskets.asStateFlow()
  val currentFlightTypes = _currentFlightTypes.asStateFlow()
  val currentVehicles = _currentVehicles.asStateFlow()

  fun refresh() {
    refreshUserAndFlights()
    refreshCurrentBalloons()
    refreshCurrentBaskets()
    refreshCurrentFlightTypes()
    refreshCurrentVehicles()
  }

  fun refreshUserAndFlights() {
    viewModelScope.launch {
      _currentUser.value = repository.userTable.get(userId ?: UNSET_ID, onError = { onError(it) })
      if (_currentUser.value is Admin) {
        _currentFlights.value = repository.flightTable.getAll(onError = { onError(it) })
      } else if (_currentUser.value is Pilot || _currentUser.value is Crew) {
        _currentFlights.value =
            repository.userTable.retrieveAssignedFlights(
                repository.flightTable, userId ?: UNSET_ID, onError = { onError(it) })
      }
    }
  }

  fun refreshCurrentBalloons() =
      viewModelScope.launch {
        _currentBalloons.value = repository.balloonTable.getAll(onError = { onError(it) })
      }

  fun refreshCurrentVehicles() =
      viewModelScope.launch {
        _currentVehicles.value = repository.vehicleTable.getAll(onError = { onError(it) })
      }

  fun refreshCurrentBaskets() =
      viewModelScope.launch {
        _currentBaskets.value = repository.basketTable.getAll(onError = { onError(it) })
      }

  fun refreshCurrentFlightTypes() =
      viewModelScope.launch {
        _currentFlightTypes.value = repository.flightTypeTable.getAll(onError = { onError(it) })
      }

  /**
   * modifies the flight by deleting the old flight and adding a new one in the db and the viewmodel
   */
  fun modifyFlight(
      newFlight: Flight,
  ) = viewModelScope.launch { repository.flightTable.update(newFlight.id, newFlight) }

  fun deleteFlight(flightId: String) =
      viewModelScope.launch { repository.flightTable.delete(flightId, onError = { onError(it) }) }

  /** adds the given flight to the db and the viewmodel */
  fun addFlight(
      flight: PlannedFlight,
  ) =
      viewModelScope.launch {
        val flightId = repository.flightTable.add(flight, onError = { onError(it) })
      }

  fun getFlight(flightId: String): StateFlow<Flight?> {
    return _currentFlights
        .map { flights -> flights?.find { it.id == flightId } }
        .stateIn(scope = viewModelScope, started = WhileUiSubscribed, initialValue = null)
  }

  /** Callback executed when an error occurs on database-related operations */
  private fun onError(e: Exception) {
    // TODO: display error message
  }

  init {
    refresh()
  }
}
