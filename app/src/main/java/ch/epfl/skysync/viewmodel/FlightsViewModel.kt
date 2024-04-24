package ch.epfl.skysync.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.database.tables.BalloonTable
import ch.epfl.skysync.database.tables.BasketTable
import ch.epfl.skysync.database.tables.FlightTable
import ch.epfl.skysync.database.tables.FlightTypeTable
import ch.epfl.skysync.database.tables.VehicleTable
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.util.WhileUiSubscribed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** ViewModel for the user */
class FlightsViewModel(
    private val flightTable: FlightTable,
    private val balloonTable: BalloonTable,
    private val basketTable: BasketTable,
    private val flightTypeTable: FlightTypeTable,
    private val vehicleTable: VehicleTable,
) : ViewModel() {
  companion object {
    @Composable
    fun createViewModel(
        flightTable: FlightTable,
        balloonTable: BalloonTable,
        basketTable: BasketTable,
        flightTypeTable: FlightTypeTable,
        vehicleTable: VehicleTable,
    ): FlightsViewModel {
      return viewModel<FlightsViewModel>(
          factory =
              object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                  return FlightsViewModel(
                      flightTable,
                      balloonTable,
                      basketTable,
                      flightTypeTable,
                      vehicleTable,
                  )
                      as T
                }
              })
    }
  }

  private val _currentFlights: MutableStateFlow<List<Flight>> = MutableStateFlow(emptyList())
  private val _currentBalloons: MutableStateFlow<List<Balloon>> = MutableStateFlow(emptyList())
  private val _currentBaskets: MutableStateFlow<List<Basket>> = MutableStateFlow(emptyList())
  private val _currentFlightTypes: MutableStateFlow<List<FlightType>> =
      MutableStateFlow(emptyList())
  private val _currentVehicles: MutableStateFlow<List<Vehicle>> = MutableStateFlow(emptyList())

  val currentFlights = _currentFlights.asStateFlow()
  val currentBalloons = _currentBalloons.asStateFlow()
  val currentBaskets = _currentBaskets.asStateFlow()
  val currentFlightTypes = _currentFlightTypes.asStateFlow()
  val currentVehicles = _currentVehicles.asStateFlow()

  fun refresh() {
    refreshCurrentFlights()
    refreshCurrentBalloons()
    refreshCurrentBaskets()
    refreshCurrentFlightTypes()
    refreshCurrentVehicles()
  }

  fun refreshCurrentBalloons() =
      viewModelScope.launch {
        _currentBalloons.value = balloonTable.getAll(onError = { onError(it) })
      }

  fun refreshCurrentVehicles() =
      viewModelScope.launch {
        _currentVehicles.value = vehicleTable.getAll(onError = { onError(it) })
      }

  fun refreshCurrentBaskets() =
      viewModelScope.launch {
        _currentBaskets.value = basketTable.getAll(onError = { onError(it) })
      }

  fun refreshCurrentFlightTypes() =
      viewModelScope.launch {
        _currentFlightTypes.value = flightTypeTable.getAll(onError = { onError(it) })
      }

  fun refreshCurrentFlights() =
      viewModelScope.launch {
        // todo: check for dirty data (flights added/modified/deleted while offline)
        _currentFlights.value = flightTable.getAll(onError = { onError(it) })
      }

  /**
   * modifies the flight by deleting the old flight and adding a new one in the db and the viewmodel
   */
  fun modifyFlight(
      newFlight: PlannedFlight,
  ) =
      viewModelScope.launch {
        flightTable.update(newFlight.id, newFlight)
        _currentFlights.value =
            _currentFlights.value.map { if (it.id == newFlight.id) newFlight else it }
      }

  /** deletes the given flight from the db and the viewmodel */
  fun deleteFlight(
      flight: Flight,
  ) =
      viewModelScope.launch {
        flightTable.delete(flight.id, onError = { onError(it) })
        _currentFlights.value -= flight
      }

  fun deleteFlight(flightId: String) =
      viewModelScope.launch {
        flightTable.delete(flightId, onError = { onError(it) })
        _currentFlights.value = currentFlights.value.filter { it.id != flightId }
      }

  /** adds the given flight to the db and the viewmodel */
  fun addFlight(
      flight: PlannedFlight,
  ) =
      viewModelScope.launch {
        val flightId = flightTable.add(flight, onError = { onError(it) })

        _currentFlights.value += flight.setId(flightId)
        refreshCurrentFlights()
      }

  /** return the flight with flight id if it exists in the list of current flights */
  private fun getFlightFromId(flightId: String): Flight? {
    return currentFlights.value.find { it.id == flightId }
  }
    fun getFlight(flightId: String): StateFlow<Flight?> {
        return _currentFlights.map{flights ->
            flights.find { it.id == flightId }
        }.stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = null
        )


    }

  /** Callback executed when an error occurs on database-related operations */
  private fun onError(e: Exception) {
    // TODO: display error message
  }

  init {
    refresh()
  }
}
