package ch.epfl.skysync.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ch.epfl.skysync.Repository
import ch.epfl.skysync.screens.CalendarScreen
import ch.epfl.skysync.viewmodel.CalendarViewModel
import com.google.firebase.auth.FirebaseUser

fun NavGraphBuilder.personalCalendar(repository: Repository, navController: NavHostController, user: FirebaseUser?) {
  navigation(startDestination = Route.AVAILABILITY_CALENDAR, route = Route.CALENDAR) {
    composable(Route.AVAILABILITY_CALENDAR) {
      val viewModel = CalendarViewModel.createViewModel(user!!, repository.userTable, repository.availabilityTable, repository.flightTable)
      CalendarScreen(navController, Route.AVAILABILITY_CALENDAR, viewModel)
    }
    composable(Route.PERSONAL_FLIGHT_CALENDAR) {
      val viewModel = CalendarViewModel.createViewModel(user!!, repository.userTable, repository.availabilityTable, repository.flightTable)
      CalendarScreen(navController, Route.PERSONAL_FLIGHT_CALENDAR, viewModel)
    }
  }
}
