package ch.epfl.skysync.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.database.ParallelOperationsEndCallback
import ch.epfl.skysync.database.tables.AvailabilityTable
import ch.epfl.skysync.database.tables.FlightTable
import ch.epfl.skysync.database.tables.UserTable
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.user.User
import ch.epfl.skysync.util.WhileUiSubscribed
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class CalendarUiState(
    val user: User? = null,
    val flightGroupCalendar: FlightGroupCalendar? = null,
    val isLoading: Boolean = false,
)

/**
 * ViewModel for the user
 *
 * @param firebaseUser: FirebaseUser? the firebase user
 */
class CalendarViewModel(
    firebaseUser: FirebaseUser,
    private val userTable: UserTable,
    private val availabilityTable: AvailabilityTable,
    private val flightTable: FlightTable
) : ViewModel() {
    companion object {
        /** creates a view model by accepting the firebase user as an argument */
        @Composable
        fun createViewModel(
            firebaseUser: FirebaseUser,
            userTable: UserTable,
            availabilityTable: AvailabilityTable,
            flightTable: FlightTable
        ): CalendarViewModel {
            return viewModel<CalendarViewModel>(
                factory =
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return CalendarViewModel(
                            firebaseUser,
                            userTable,
                            availabilityTable,
                            flightTable
                        ) as T
                    }
                })
        }
    }

    private val _user: MutableStateFlow<User?> = MutableStateFlow(null)
    private val _flightGroupCalendar: MutableStateFlow<FlightGroupCalendar?> =
        MutableStateFlow(null)
    private val _isLoading = MutableStateFlow(false)

    private val uid: String
    private var originalAvailabilityCalendar = AvailabilityCalendar()

    val uiState: StateFlow<CalendarUiState> = combine(
        _user, _flightGroupCalendar, _isLoading
    ) { user, flightGroupCalendar, isLoading ->
        CalendarUiState(user, flightGroupCalendar, isLoading)
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = CalendarUiState(isLoading = true)
    )

    init {
        uid = firebaseUser.uid
        refreshUser()
    }

    private fun refreshUser() {
        _isLoading.value = true
        userTable.get(uid, {
            if (it == null) {
                // display not found message
            } else {
                _isLoading.value = false
                _user.value = it
                originalAvailabilityCalendar = it.availabilities.copy()
            }
        }, this::onError)
    }

    private fun onError(e: Exception) {
        // display error message
    }

    fun saveAvailabilities() {
        val user = _user.value ?: return
        val availabilityCalendar = user.availabilities

        _isLoading.value = true

        val delayedCallback = ParallelOperationsEndCallback(availabilityCalendar.getSize()) {
            // we refresh the user to have the latest version of the availability calendar
            // as at the moment the Availability.id is lost when changing the availability status
            // (see AvailabilityCalendar.setAvailabilityByDate)
            // By doing it that way we have the IDs of all availabilities and we reset the
            // originalAvailabilityCalendar attribute
            refreshUser()
        }

        for (availability in availabilityCalendar.getAvailabilities()) {
            val originalAvailability =
                originalAvailabilityCalendar.getByDate(availability.date, availability.timeSlot)
            if (availability != originalAvailability) {
                if (originalAvailability == null) {
                    availabilityTable.add(user.id, availability, {
                        delayedCallback.run()
                    }, this::onError)
                } else if (availability.status == AvailabilityStatus.UNDEFINED) {
                    availabilityTable.delete(originalAvailability.id, {
                        delayedCallback.run()
                    }, this::onError)
                } else {
                    // update availability
                }
            }
        }
    }
}