package ch.epfl.skysync.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.SnackbarManager
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.CalendarDifferenceType
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.user.User
import java.time.LocalDate
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Calendar screen
 *
 * @param uid The Firebase authentication uid of the user
 * @param repository App repository
 */
class CalendarViewModel(
    private val uid: String,
    repository: Repository,
) : ViewModel() {
  companion object {
    @Composable
    fun createViewModel(
        uid: String,
        repository: Repository,
    ): CalendarViewModel {
      return viewModel<CalendarViewModel>(
          factory =
              object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                  return CalendarViewModel(uid, repository) as T
                }
              })
    }
  }

  private val userTable = repository.userTable
  private val availabilityTable = repository.availabilityTable
  private val flightTable = repository.flightTable

  private val _user: MutableStateFlow<User?> = MutableStateFlow(null)
  val user: StateFlow<User?> = _user.asStateFlow()
  private var originalAvailabilityCalendar = AvailabilityCalendar()
  private val _currentAvailabilityCalendar = MutableStateFlow(value = AvailabilityCalendar())
  val currentAvailabilityCalendar = _currentAvailabilityCalendar.asStateFlow()

  private var _currentFlightGroupCalendar = MutableStateFlow(FlightGroupCalendar())
  val currentFlightGroupCalendar = _currentFlightGroupCalendar.asStateFlow()

  init {
    refresh()
  }

  /**
   * Fetch the user from the database.
   *
   * Update asynchronously the [CalendarUiState.user] reference
   *
   * (Starts a new coroutine)
   */
  fun refresh() = viewModelScope.launch { refreshUserAndCalendars() }

  /**
   * Fetch the user from the database.
   *
   * Update asynchronously the [CalendarUiState.user] reference
   */
  private suspend fun refreshUserAndCalendars() {
    _user.value = userTable.get(uid, this::onError)!!
    var currentAvCal = AvailabilityCalendar(userTable.retrieveAvailabilities(uid, this::onError))
    val flights = userTable.retrieveAssignedFlights(flightTable, uid, this::onError)
    flights.forEach { flight ->
      if (flight !is FinishedFlight) {
        currentAvCal =
            AvailabilityCalendar(
                currentAvCal
                    .setAvailabilityByDate(
                        flight.date, flight.timeSlot, AvailabilityStatus.ASSIGNED)
                    .cells)
      }
    }
    _currentAvailabilityCalendar.value = currentAvCal
    originalAvailabilityCalendar = currentAvCal

    _currentFlightGroupCalendar.value = FlightGroupCalendar.fromFlightList(flights)
  }

  /** Callback executed when an error occurs on database-related operations */
  private fun onError(e: Exception) {
    if (e !is CancellationException) {
      SnackbarManager.showMessage(e.message ?: "An unknown error occurred")
    }
  }

  fun setToNextAvailabilityStatus(date: LocalDate, time: TimeSlot) {
    val oldCalendar = _currentAvailabilityCalendar.value
    val newCalendar = oldCalendar.setToNextAvailabilityStatus(date, time)
    _currentAvailabilityCalendar.value = newCalendar as AvailabilityCalendar
  }

  /**
   * Save the current availability calendar to the database by adding, updating, deleting
   * availabilities as needed
   */
  fun saveAvailabilities() =
      viewModelScope.launch {
        val user = _user.value ?: return@launch

        val differences =
            originalAvailabilityCalendar.getDifferencesWithOtherCalendar(
                _currentAvailabilityCalendar.value)
        if (!differences.isEmpty()) {
          val jobs = mutableListOf<Job>()

          for ((difference, availability) in differences) {
            when (difference) {
              CalendarDifferenceType.ADDED -> {
                jobs.add(
                    launch {
                      availabilityTable.add(user.id, availability, onError = { onError(it) })
                    })
              }
              CalendarDifferenceType.UPDATED -> {
                jobs.add(
                    launch {
                      availabilityTable.update(
                          user.id, availability.id, availability, onError = { onError(it) })
                    })
              }
              CalendarDifferenceType.DELETED -> {
                jobs.add(
                    launch { availabilityTable.delete(availability.id, onError = { onError(it) }) })
              }
            }
          }
          jobs.forEach { it.join() }
        }
        refreshUserAndCalendars()
      }

  /** Cancels the modification made in the current availability calendar */
  fun cancelAvailabilities(): Unit {
    if (_currentAvailabilityCalendar.value != originalAvailabilityCalendar)
        _currentAvailabilityCalendar.value = originalAvailabilityCalendar
  }
}
