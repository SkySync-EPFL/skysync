package ch.epfl.skysync.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ch.epfl.skysync.screens.CalendarScreen
import ch.epfl.skysync.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseUser

fun NavGraphBuilder.personalCalendar(
    navController: NavHostController,
    user: FirebaseUser?
){
    navigation(
        startDestination = Route.AVAILABILITY_CALENDAR,
        route = Route.CALENDAR
    )
    {
        composable(Route.AVAILABILITY_CALENDAR) {
            val viewModel = UserViewModel.createViewModel(user)
            CalendarScreen(navController, Route.AVAILABILITY_CALENDAR, viewModel)
        }
        composable(Route.PERSONAL_FLIGHT_CALENDAR) {
            val viewModel = UserViewModel.createViewModel(user)
            CalendarScreen(navController, Route.PERSONAL_FLIGHT_CALENDAR, viewModel)
        }
    }

}