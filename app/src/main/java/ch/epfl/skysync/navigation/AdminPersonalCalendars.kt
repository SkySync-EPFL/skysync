package ch.epfl.skysync.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.ConnectivityStatus
import ch.epfl.skysync.screens.admin.AdminCalendarScreen
import ch.epfl.skysync.viewmodel.CalendarViewModel

/**
 * The graph for the calendar of an admin user
 *
 * @param repository The repository
 * @param navController The navigation controller
 * @param uid The user ID
 * @param connectivityStatus The connectivity status
 */
fun NavGraphBuilder.adminpersonalCalendar(
    repository: Repository,
    navController: NavHostController,
    uid: String?,
    connectivityStatus: ConnectivityStatus
) {
  navigation(startDestination = Route.ADMIN_AVAILABILITY_CALENDAR, route = Route.ADMIN_CALENDAR) {
    composable(Route.ADMIN_AVAILABILITY_CALENDAR) {
      val viewModel = CalendarViewModel.createViewModel(uid!!, repository)
      AdminCalendarScreen(
          navController, Route.ADMIN_AVAILABILITY_CALENDAR, viewModel, connectivityStatus)
    }
    composable(Route.ADMIN_FLIGHT_CALENDAR) {
      val viewModel = CalendarViewModel.createViewModel(uid!!, repository)
      AdminCalendarScreen(navController, Route.ADMIN_FLIGHT_CALENDAR, viewModel, connectivityStatus)
    }
  }
}
