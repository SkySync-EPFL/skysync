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
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.FlightStatus
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.message.MessageGroup
import ch.epfl.skysync.models.user.Admin
import ch.epfl.skysync.models.user.User
import ch.epfl.skysync.util.WhileUiSubscribed
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** ViewModel for the user */
class FlightsViewModel(val repository: Repository, val userId: String?, val flightId: String?) :
    ViewModel() {
  companion object {
    @Composable
    fun createViewModel(
        repository: Repository,
        userId: String?,
        flightId: String? = null
    ): FlightsViewModel {
      return viewModel<FlightsViewModel>(
          factory =
              object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                  return FlightsViewModel(repository, userId, flightId) as T
                }
              })
    }
  }

  var date: LocalDate? = null
    private set

  var timeSlot: TimeSlot? = null
    private set

  // flights relevant to exist for the home screen
  private val _currentFlights: MutableStateFlow<List<Flight>?> = MutableStateFlow(null)
  // all flights a user has ever been affected to
  private val _allAffectedFlights: MutableStateFlow<List<Flight>?> = MutableStateFlow(null)

  private val _availableBalloons: MutableStateFlow<List<Balloon>> = MutableStateFlow(emptyList())
  private val _availableBaskets: MutableStateFlow<List<Basket>> = MutableStateFlow(emptyList())
  private val _currentFlightTypes: MutableStateFlow<List<FlightType>> =
      MutableStateFlow(emptyList())
  private val _availableVehicles: MutableStateFlow<List<Vehicle>> = MutableStateFlow(emptyList())
  private val _currentUser = MutableStateFlow<User?>(null)
  private val _availableUsers = MutableStateFlow(emptyList<User>())
  private val _flight = MutableStateFlow<Flight?>(null)

  val currentFlights = _currentFlights.asStateFlow()
  val affectedFlights = _allAffectedFlights.asStateFlow()
  val currentBalloons = _availableBalloons.asStateFlow()
  val currentBaskets = _availableBaskets.asStateFlow()
  val currentFlightTypes = _currentFlightTypes.asStateFlow()
  val currentVehicles = _availableVehicles.asStateFlow()
  val currentUser = _currentUser.asStateFlow()
  val availableUsers = _availableUsers.asStateFlow()
  val flight = _flight.asStateFlow()

  fun refresh() {
    refreshUserAndFlights()
    refreshAvailableBalloons()
    refreshAvailableBaskets()
    refreshCurrentFlightTypes()
    refreshAvailableVehicles()
    refreshAvailableUsers()
  }

  private fun refreshFilteredByDateAndTimeSlot() {
    refreshAvailableBalloons()
    refreshAvailableBaskets()
    refreshAvailableVehicles()
    refreshAvailableUsers()
  }

  fun setDateAndTimeSlot(date: LocalDate, timeSlot: TimeSlot) {
    this.date = date
    this.timeSlot = timeSlot
    refreshFilteredByDateAndTimeSlot()
  }

  fun setFlight(flight: Flight) {
    _flight.value = flight
    setDateAndTimeSlot(flight.date, flight.timeSlot)
  }

  /**
   * refreshes the user and the flights. Finished flights are filtered to contain only the
   * uncompleted ones
   */
  fun refreshUserAndFlights() =
      viewModelScope.launch {
        _currentUser.value = repository.userTable.get(userId!!, onError = { onError(it) })
        lateinit var fetchedFlights: List<Flight>
        if (_currentUser.value!! is Admin) {
          Log.d("FlightsViewModel", "Admin user loaded")
          fetchedFlights = repository.flightTable.getAll(onError = { onError(it) })
        } else {
          fetchedFlights =
              repository.userTable.retrieveAssignedFlights(
                  repository.flightTable, userId, onError = { onError(it) })
          Log.d("FlightsViewModel", "Pilot or Crew user loaded")
        }
        _allAffectedFlights.value = fetchedFlights
        _currentFlights.value =
            FlightStatus.filterCompletedFlights(fetchedFlights, _currentUser.value!!)
      }

  /** updates */
  private fun hasDateAndTimeSlot(): Boolean {
    return date != null && timeSlot != null
  }

  private fun needsToAddCurrentlyAffected(notNull: Any?): Boolean {
    return notNull != null &&
        _flight.value != null &&
        _flight.value!!.date == date &&
        _flight.value!!.timeSlot == timeSlot
  }

  private fun refreshAvailableBalloons() =
      viewModelScope.launch {
        if (hasDateAndTimeSlot()) {
          _availableBalloons.value =
              repository.balloonTable.getBalloonsAvailableOn(
                  flightTable = repository.flightTable,
                  localDate = date!!,
                  timeslot = timeSlot!!,
                  onError = { onError(it) })

          if (needsToAddCurrentlyAffected(_flight.value?.balloon)) {
            val availableBalloons = _availableBalloons.value.filter { it != _flight.value?.balloon }
            _availableBalloons.value =
                availableBalloons.plus(_flight.value?.balloon!!).sortedBy { it.name }
          }
        } else {
          _availableBalloons.value =
              repository.balloonTable.getAll(onError = { onError(it) }).sortedBy { it.name }
        }
      }

  private fun refreshAvailableUsers() =
      viewModelScope.launch {
        if (hasDateAndTimeSlot()) {
          _availableUsers.value =
              repository.userTable.getUsersAvailableOn(
                  flightTable = repository.flightTable,
                  localDate = date!!,
                  timeslot = timeSlot!!,
                  onError = { onError(it) })
          if (needsToAddCurrentlyAffected(flight.value?.team)) {
            val flightUsers = flight.value?.team!!.getUsers()
            val availableUsers = _availableUsers.value.filter { !flightUsers.contains(it) }
            _availableUsers.value =
                availableUsers.plus(flight.value?.team!!.getUsers()).sortedBy { it.lastname }
          }
        } else {
          _availableUsers.value =
              repository.userTable.getAll(onError = { onError(it) }).sortedBy { it.lastname }
        }
      }

  private fun refreshAvailableVehicles() =
      viewModelScope.launch {
        if (hasDateAndTimeSlot()) {
          _availableVehicles.value =
              repository.vehicleTable.getVehiclesAvailableOn(
                  flightTable = repository.flightTable,
                  localDate = date!!,
                  timeslot = timeSlot!!,
                  onError = { onError(it) })

          if (needsToAddCurrentlyAffected(flight.value?.vehicles)) {
            val availableVehicles =
                _availableVehicles.value.filter { !flight.value?.vehicles!!.contains(it) }
            _availableVehicles.value =
                availableVehicles.plus(flight.value?.vehicles!!).sortedBy { it.name }
          }
        } else {
          _availableVehicles.value =
              repository.vehicleTable.getAll(onError = { onError(it) }).sortedBy { it.name }
        }
      }

  private fun refreshAvailableBaskets() =
      viewModelScope.launch {
        if (hasDateAndTimeSlot()) {
          _availableBaskets.value =
              repository.basketTable.getBasketsAvailableOn(
                  flightTable = repository.flightTable,
                  localDate = date!!,
                  timeslot = timeSlot!!,
                  onError = { onError(it) })

          if (needsToAddCurrentlyAffected(flight.value?.basket)) {
            val availableBaskets = _availableBaskets.value.filter { it != flight.value?.basket!! }
            _availableBaskets.value =
                availableBaskets.plus(flight.value?.basket!!).sortedBy { it.name }
          }
        } else {
          _availableBaskets.value =
              repository.basketTable.getAll(onError = { onError(it) }).sortedBy { it.name }
        }
      }

  private fun refreshCurrentFlightTypes() =
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
  ) = viewModelScope.launch { repository.flightTable.add(flight, onError = { onError(it) }) }

  private fun groupName(date: LocalDate, timeSlot: TimeSlot): String {
    return "Flight: ${date.format(DateTimeFormatter.ofPattern("dd/MM"))} $timeSlot"
  }

  /** updates the planned flight to a confirmed flight */
  fun addConfirmedFlight(flight: ConfirmedFlight) =
      viewModelScope.launch {
        repository.flightTable.update(flight.id, flight, onError = { onError(it) })
        val flightChatGroup =
            MessageGroup(
                UNSET_ID,
                groupName(flight.date, flight.timeSlot),
                flight.color,
                flight.team.getUsers().map { it.id }.toSet())
        repository.messageGroupTable.add(flightChatGroup, onError = { onError(it) })
      }

  fun getFlight(flightId: String): StateFlow<Flight?> {
    return _allAffectedFlights
        .map { flights -> flights?.find { it.id == flightId } }
        .stateIn(scope = viewModelScope, started = WhileUiSubscribed, initialValue = null)
  }

  /** Callback executed when an error occurs on database-related operations */
  private fun onError(e: Exception) {
    if (e !is CancellationException) {
      SnackbarManager.showMessage(e.message ?: "An unknown error occurred")
    }
  }

  init {
    refresh()
  }
}
