package ch.epfl.skysync.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.database.ParallelOperationsEndCallback
import ch.epfl.skysync.database.tables.AvailabilityTable
import ch.epfl.skysync.database.tables.UserTable
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.CalendarDifferenceType
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.user.User
import ch.epfl.skysync.util.WhileUiSubscribed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class CalendarUiState(
    val user: User? = null,
    val availabilityCalendar: AvailabilityCalendar = AvailabilityCalendar(),
    val flightGroupCalendar: FlightGroupCalendar = FlightGroupCalendar(),
    val isLoading: Boolean = false,
)

/**
 * ViewModel for the Calendar screen
 *
 * @param uid The Firebase authentication uid of the user
 * @param userTable The user table
 * @param availabilityTable The availability table
 */
class CalendarViewModel(
    private val uid: String,
    private val userTable: UserTable,
    private val availabilityTable: AvailabilityTable,
) : ViewModel() {
  companion object {
    /** Creates a view model by accepting the firebase user as an argument */
    @Composable
    fun createViewModel(
        uid: String,
        userTable: UserTable,
        availabilityTable: AvailabilityTable,
    ): CalendarViewModel {
      return viewModel<CalendarViewModel>(
          factory =
              object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                  return CalendarViewModel(uid, userTable, availabilityTable) as T
                }
              })
    }
  }

  private val user: MutableStateFlow<User?> = MutableStateFlow(null)
  private val loadingCounter = MutableStateFlow(0)

  private var originalAvailabilityCalendar = AvailabilityCalendar()

  val uiState: StateFlow<CalendarUiState> =
      combine(user, loadingCounter) { user, loadingCounter ->
            CalendarUiState(
                user,
                user?.availabilities ?: AvailabilityCalendar(),
                user?.assignedFlights ?: FlightGroupCalendar(),
                loadingCounter > 0)
          }
          .stateIn(
              scope = viewModelScope,
              started = WhileUiSubscribed,
              initialValue = CalendarUiState(isLoading = true))

  init {
    refreshUser()
  }

  /** Fetch the user from the database Update asynchronously the [CalendarUiState.user] reference */
  private fun refreshUser() {
    loadingCounter.value += 1
    userTable.get(
        uid,
        {
          if (it == null) {
            // TODO: display not found message
          } else {
            user.value = it
            originalAvailabilityCalendar = it.availabilities.copy()
            loadingCounter.value -= 1
          }
        },
        this::onError)
  }

  /** Callback executed when an error occurs on database-related operations */
  private fun onError(e: Exception) {
    // TODO: display error message
  }

  /**
   * Save the current availability calendar to the database by adding, updating, deleting
   * availabilities as needed
   */
  fun saveAvailabilities() {
    val user = user.value ?: return
    val availabilityCalendar = user.availabilities

    val differences =
        originalAvailabilityCalendar.getDifferencesWithOtherCalendar(availabilityCalendar)

    loadingCounter.value += 1

    val delayedCallback =
        ParallelOperationsEndCallback(differences.size) {
          loadingCounter.value -= 1
          // we refresh the user to have the latest version of the availability calendar
          // as at the moment the Availability.id is lost when changing the availability status
          // (see AvailabilityCalendar.setAvailabilityByDate)
          // By doing it that way we have the IDs of all availabilities and we reset the
          // originalAvailabilityCalendar attribute
          refreshUser()
        }

    for ((difference, availability) in differences) {
      when (difference) {
        CalendarDifferenceType.ADDED -> {
          availabilityTable.add(user.id, availability, { delayedCallback.run() }, this::onError)
        }
        CalendarDifferenceType.UPDATED -> {
          // TODO: update availability
        }
        CalendarDifferenceType.DELETED -> {
          availabilityTable.delete(availability.id, { delayedCallback.run() }, this::onError)
        }
      }
    }
  }
}
