package ch.epfl.skysync.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.SnackbarManager
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.CalendarDifferenceType
import ch.epfl.skysync.models.calendar.CalendarModel
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.user.User
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

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
  private var _currentAvailabilityCalendar =  MutableStateFlow(AvailabilityCalendar())
    val currentAvailabilityCalendar: StateFlow<AvailabilityCalendar> = _currentAvailabilityCalendar.asStateFlow()

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
  fun refresh() = viewModelScope.launch { refreshUser() }

  /**
   * Fetch the user from the database.
   *
   * Update asynchronously the [CalendarUiState.user] reference
   */
  private suspend fun refreshUser() {
    _user.value = userTable.get(uid, this::onError)!!
    _currentAvailabilityCalendar.value = AvailabilityCalendar(userTable.retrieveAvailabilities(uid, this::onError))
    originalAvailabilityCalendar = _currentAvailabilityCalendar.value.copy()
      _currentFlightGroupCalendar.value = FlightGroupCalendar.fromFlightList(userTable.retrieveAssignedFlights(flightTable, uid, this::onError))
  }

  /** Callback executed when an error occurs on database-related operations */
  private fun onError(e: Exception) {
    SnackbarManager.showMessage(e.message ?: "An unknown error occurred")
  }

    fun setToNextAvailabilityStatus(date: LocalDate, time: TimeSlot) {
        _currentAvailabilityCalendar.value = _currentAvailabilityCalendar.value.setToNextAvailabilityStatus(date, time) as AvailabilityCalendar
    }


  /**
   * Save the current availability calendar to the database by adding, updating, deleting
   * availabilities as needed
   */
  fun saveAvailabilities() =
      viewModelScope.launch {
        val user = _user.value ?: return@launch

        val differences =
            originalAvailabilityCalendar.getDifferencesWithOtherCalendar(_currentAvailabilityCalendar.value)

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

        // we refresh the user to make sure that we have the latest version of
        // the availability calendar, by doing it that way we also have the IDs
        // of all availabilities and we reset the originalAvailabilityCalendar attribute
        // However this might be unnecessary
        refreshUser()
      }

  /** Cancels the modification made in the current availability calendar */
  fun cancelAvailabilities() = viewModelScope.launch {
      refreshUser()
  }
}
