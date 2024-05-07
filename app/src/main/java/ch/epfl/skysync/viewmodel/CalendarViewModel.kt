package ch.epfl.skysync.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.database.tables.AvailabilityTable
import ch.epfl.skysync.database.tables.FlightTable
import ch.epfl.skysync.database.tables.UserTable
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.CalendarDifferenceType
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.user.Admin
import ch.epfl.skysync.models.user.User
import ch.epfl.skysync.util.WhileUiSubscribed
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
    private val flightTable: FlightTable
) : ViewModel() {
  companion object {
    @Composable
    fun createViewModel(
        uid: String,
        userTable: UserTable,
        availabilityTable: AvailabilityTable,
        flightTable: FlightTable
    ): CalendarViewModel {
      return viewModel<CalendarViewModel>(
          factory =
              object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                  return CalendarViewModel(uid, userTable, availabilityTable, flightTable) as T
                }
              })
    }
  }

  private val user: MutableStateFlow<User?> = MutableStateFlow(null)
  private val loadingCounter = MutableStateFlow(0)

  private var originalAvailabilityCalendar = AvailabilityCalendar()

  val uiState: StateFlow<CalendarUiState> =
      combine(user, loadingCounter) { user, loadingCounter ->
            val flights =
                userTable.retrieveAssignedFlights(
                    flightTable, user?.id ?: "", onError = { onError(it) })

            CalendarUiState(
                user,
                user?.availabilities ?: AvailabilityCalendar(),
                FlightGroupCalendar().addFlightList(flights),
                loadingCounter > 0)
          }
          .stateIn(
              scope = viewModelScope,
              started = WhileUiSubscribed,
              initialValue = CalendarUiState(isLoading = true))

  init {
    refresh()
  }

  /**
   * Fetch the user from the database. Update asynchronously the [CalendarUiState.user] reference
   *
   * (Starts a new coroutine)
   */
  fun refresh() = viewModelScope.launch { refreshUser() }

  /** Fetch the user from the database Update asynchronously the [CalendarUiState.user] reference */
  private suspend fun refreshUser() {
    loadingCounter.value += 1
    var newUser = userTable.get(uid, this::onError)
    loadingCounter.value -= 1
    if (newUser == null) {
      newUser =
          Admin(
              uid,
              "unknown",
              "unknown",
              "",
              AvailabilityCalendar(),
              FlightGroupCalendar(),
          )
      loadingCounter.value += 1
      userTable.set(uid, newUser, {})
      loadingCounter.value -= 1
    }
    user.value = newUser
    originalAvailabilityCalendar = newUser.availabilities.copy()
  }

  /** Callback executed when an error occurs on database-related operations */
  private fun onError(e: Exception) {
    // TODO: display error message
  }

  /**
   * Save the current availability calendar to the database by adding, updating, deleting
   * availabilities as needed
   */
  fun saveAvailabilities() =
      viewModelScope.launch {
        val user = user.value ?: return@launch
        val availabilityCalendar = user.availabilities

        val differences =
            originalAvailabilityCalendar.getDifferencesWithOtherCalendar(availabilityCalendar)

        loadingCounter.value += 1

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

        loadingCounter.value -= 1
        // we refresh the user to make sure that we have the latest version of
        // the availability calendar, by doing it that way we also have the IDs
        // of all availabilities and we reset the originalAvailabilityCalendar attribute
        // However this might be unnecessary
        refreshUser()
      }
}
