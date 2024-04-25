package ch.epfl.skysync.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ch.epfl.skysync.Repository
import ch.epfl.skysync.screens.CalendarScreen
import ch.epfl.skysync.viewmodel.CalendarViewModel

fun NavGraphBuilder.personalCalendar(
    repository: Repository,
    navController: NavHostController,
    uid: String?
) {
  navigation(startDestination = Route.AVAILABILITY_CALENDAR, route = Route.CALENDAR) {
    composable(Route.AVAILABILITY_CALENDAR) {
      val viewModel =
          CalendarViewModel.createViewModel(
              uid!!, repository.userTable, repository.availabilityTable)
      CalendarScreen(navController, Route.AVAILABILITY_CALENDAR, viewModel)
    }
    composable(Route.FLIGHT_CALENDAR) {
      val viewModel =
          CalendarViewModel.createViewModel(
              uid!!, repository.userTable, repository.availabilityTable)
      CalendarScreen(navController, Route.FLIGHT_CALENDAR, viewModel)
    }
  }
}
