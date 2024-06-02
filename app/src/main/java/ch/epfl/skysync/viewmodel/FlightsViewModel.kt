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
import ch.epfl.skysync.models.calendar.Availability
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.FinishedFlight
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

/**
 * ViewModel for the flights
 *
 * @param repository The app repository
 * @param userId The user id
 * @param flightId The flight id
 * @return The flights view model
 */
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

  /** fetches all the informations needed */
  fun refresh() {
    refreshUserAndFlights()
    refreshAvailableBalloons()
    refreshAvailableBaskets()
    refreshCurrentFlightTypes()
    refreshAvailableVehicles()
    refreshAvailableUsers()
  }

  /**
   * fetches the user, balloons, baskets and vehicles according to their availability on the given
   * date and time slot
   */
  private fun refreshFilteredByDateAndTimeSlot() {
    refreshAvailableBalloons()
    refreshAvailableBaskets()
    refreshAvailableVehicles()
    refreshAvailableUsers()
  }

  /**
   * Sets the date and time slot and refreshes the filtered information
   *
   * @param date The date of the flight
   * @param timeSlot The time slot of the flight
   */
  fun setDateAndTimeSlot(date: LocalDate, timeSlot: TimeSlot) {
    this.date = date
    this.timeSlot = timeSlot
    refreshFilteredByDateAndTimeSlot()
  }

  /**
   * sets the flight and refreshes the filtered information
   *
   * @param flight The flight to set
   */
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

  /**
   * Check if the date and time slot are set
   *
   * @return true if the date and time slot are set, false otherwise
   */
  private fun hasDateAndTimeSlot(): Boolean {
    return date != null && timeSlot != null
  }

  /**
   * Check if a given item can be added to the list of available items
   *
   * @param notNull The item to check
   * @return true if the item can be added, false otherwise
   */
  private fun needsToAddCurrentlyAffected(notNull: Any?): Boolean {
    return notNull != null &&
        _flight.value != null &&
        _flight.value!!.date == date &&
        _flight.value!!.timeSlot == timeSlot
  }

  /** Refreshes the available balloons */
  fun refreshAvailableBalloons() =
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
            _availableBalloons.value = availableBalloons.plus(_flight.value?.balloon!!)
          }
        } else {
          _availableBalloons.value = repository.balloonTable.getAll(onError = { onError(it) })
        }
      }

    /** Refreshes the available users */
    fun refreshAvailableUsers() =
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
                    _availableUsers.value = availableUsers.plus(flight.value?.team!!.getUsers())
                }
            } else {
                _availableUsers.value = repository.userTable.getAll(onError = { onError(it) })
            }
            _availableUsers.value = _availableUsers.value.sortedBy { it.name() }
        }

  /** Refreshes the available vehicles */
  fun refreshAvailableVehicles() =
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
            _availableVehicles.value = availableVehicles.plus(flight.value?.vehicles!!)
          }
        } else {
          _availableVehicles.value = repository.vehicleTable.getAll(onError = { onError(it) })
        }
        _availableVehicles.value = _availableVehicles.value.sortedBy { it.name }
      }

  /** Refreshes the available baskets */
  fun refreshAvailableBaskets() =
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
            _availableBaskets.value = availableBaskets.plus(flight.value?.basket!!)
          }
        } else {
          _availableBaskets.value = repository.basketTable.getAll(onError = { onError(it) })
        }
        _availableBaskets.value = _availableBaskets.value.sortedBy { it.name }
      }

  /** Refreshes the current flight types */
  private fun refreshCurrentFlightTypes() =
      viewModelScope.launch {
        _currentFlightTypes.value =
            repository.flightTypeTable.getAll(onError = { onError(it) }).sortedBy { it.name }
      }

  /**
   * modifies the flight by deleting the old flight and adding a new one in the db and the viewmodel
   *
   * @param newFlight The modified flight to add
   */
  fun modifyFlight(newFlight: Flight) =
      viewModelScope.launch {
        val oldFlight = flight.value
        if (oldFlight != null) {
          setUsersToNewStatus(oldFlight!!, AvailabilityStatus.OK)
        }
        repository.flightTable.update(newFlight.id, newFlight)

        setUsersToNewStatus(newFlight, AvailabilityStatus.ASSIGNED)
      }
    /**
     * Deletes the flight from the db
     *
     * @param flight The flight to delete
     */
  fun deleteFlight(flight: Flight) =
      viewModelScope.launch {
        repository.flightTable.delete(flight.id, onError = { onError(it) })
        setUsersToNewStatus(flight, AvailabilityStatus.OK)
      }
  /**
   * Adds the given flight to the db and the viewmodel
   *
   * @param flight The flight to add
   */
  fun addFlight(
      flight: PlannedFlight,
  ) =
      viewModelScope.launch {
        repository.flightTable.add(flight, onError = { onError(it) })
        setUsersToNewStatus(flight, AvailabilityStatus.ASSIGNED)
      }

  /**
   * Get the group name for the flight
   *
   * @param date The date of the flight
   * @param timeSlot The time slot of the flight
   * @return The group name
   */
  private fun groupName(date: LocalDate, timeSlot: TimeSlot): String {
    return "Flight: ${date.format(DateTimeFormatter.ofPattern("dd/MM"))} $timeSlot"
  }
    /**
     * set every user to a flight to a new status on the flight date
     *
     * @param flight The flight
     * @param status the new availability status to set
     *
     */
  private suspend fun setUsersToNewStatus(flight: Flight, status: AvailabilityStatus) {
    flight.team.getUsers().forEach { user ->
      val availability =
          repository.availabilityTable.queryByDateAndUserId(
              user.id, flight.date, flight.timeSlot, onError = { onError(it) })
      repository.availabilityTable.update(
          user.id,
          availability.id,
          Availability(
              status = status,
              timeSlot = flight.timeSlot,
              date = flight.date,
              id = availability.id))
    }
  }

  /**
   * Check if the report is done for the authenticated user
   *
   * @param flight The flight to check
   * @return true if the report is done, false otherwise
   */
  fun reportDone(flight: FinishedFlight): Boolean {
    return (flight).reportId.any { report -> report.author == userId }
  }

  /**
   * Create a confirmed flight from a planned flight
   *
   * @param flight The flight to confirm
   */
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

  /**
   * Get the flight from the db
   *
   * @param flightId The id of the flight to get
   * @return The flight
   */
  fun getFlight(flightId: String): StateFlow<Flight?> {
    return _allAffectedFlights
        .map { flights -> flights?.find { it.id == flightId } }
        .stateIn(scope = viewModelScope, started = WhileUiSubscribed, initialValue = null)
  }

  /**
   * Callback executed when an error occurs on database-related operations
   *
   * @param e The exception that occurred
   */
  private fun onError(e: Exception) {
    if (e !is CancellationException) {
      SnackbarManager.showMessage(e.message ?: "An unknown error occurred")
    }
  }

  /** Function called when the ViewModel is initialized */
  init {
    refresh()
  }
}
