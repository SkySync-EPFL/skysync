package ch.epfl.skysync.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ch.epfl.skysync.Repository
import ch.epfl.skysync.screens.admin.AdminCalendarScreen
import ch.epfl.skysync.viewmodel.CalendarViewModel

fun NavGraphBuilder.adminpersonalCalendar(
    repository: Repository,
    navController: NavHostController,
    uid: String?
) {
  navigation(startDestination = Route.ADMIN_AVAILABILITY_CALENDAR, route = Route.ADMIN_CALENDAR) {
    composable(Route.ADMIN_AVAILABILITY_CALENDAR) {
      val viewModel =
          CalendarViewModel.createViewModel(
              uid!!, repository.userTable, repository.availabilityTable)
      AdminCalendarScreen(navController, Route.AVAILABILITY_CALENDAR, viewModel)
    }
    composable(Route.ADMIN_FLIGHT_CALENDAR) {
      val viewModel =
          CalendarViewModel.createViewModel(
              uid!!, repository.userTable, repository.availabilityTable)
      AdminCalendarScreen(navController, Route.FLIGHT_CALENDAR, viewModel)
    }
  }
}
