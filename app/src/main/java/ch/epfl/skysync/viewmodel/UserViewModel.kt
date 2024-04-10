package ch.epfl.skysync.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.user.Crew
import ch.epfl.skysync.models.user.User
import com.google.firebase.auth.FirebaseUser
import java.time.LocalDate

/**
 * ViewModel for the user
 * @param firebaseUser: FirebaseUser? the firebase user
 */
class UserViewModel(firebaseUser : FirebaseUser?): ViewModel(){
    companion object {
        /**
         * creates a view model by accepting the firebase user as an argument
         */
        @Composable
        fun createViewModel(firebaseUser: FirebaseUser?): UserViewModel {
            return viewModel<UserViewModel>(
                factory = object: ViewModelProvider.Factory {
                    override fun <T: ViewModel> create(modelClass: Class<T>): T {
                        return UserViewModel(firebaseUser) as T
                    }
                }
            )
        }
    }

    lateinit var user: MutableState<User>

    init {
        initUser(firebaseUser)
    }

    /**
     * Initializes the user as a function of the firebaseUser.
     * Currently just creates a new (hardcoded) user.
     */
    private fun initUser(firebaseUser: FirebaseUser?){
        val foundUser: User? = null //TODO: fetch user from db
        user = mutableStateOf(foundUser ?: getHardcodeUser())
    }
}

/**
 * Returns a hardcoded user till synched with DB
 */
fun getHardcodeUser(): User {
    val availabilityCalendar = AvailabilityCalendar()
    val flightGroupCalendar = FlightGroupCalendar()

    return Crew(
            "John",
            "Doe",
            "1234",
            availabilityCalendar,
            flightGroupCalendar
        )


}


