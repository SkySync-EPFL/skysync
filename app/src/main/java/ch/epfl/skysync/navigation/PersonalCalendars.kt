package ch.epfl.skysync.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.ConnectivityStatus
import ch.epfl.skysync.screens.crewpilot.CalendarScreen
import ch.epfl.skysync.viewmodel.CalendarViewModel

/**
 * The graph for the calendar of a crew/pilot user
 *
 * @param repository The repository
 * @param navController The navigation controller
 * @param uid The user ID
 * @param connectivityStatus The connectivity status
 */
fun NavGraphBuilder.personalCalendar(
    repository: Repository,
    navController: NavHostController,
    uid: String?,
    connectivityStatus: ConnectivityStatus
) {
  navigation(startDestination = Route.CREW_AVAILABILITY_CALENDAR, route = Route.CREW_CALENDAR) {
    composable(Route.CREW_AVAILABILITY_CALENDAR) {
      val viewModel = CalendarViewModel.createViewModel(uid!!, repository)
      CalendarScreen(navController, Route.CREW_AVAILABILITY_CALENDAR, viewModel, connectivityStatus)
    }
    composable(Route.CREW_FLIGHT_CALENDAR) {
      val viewModel = CalendarViewModel.createViewModel(uid!!, repository)
      CalendarScreen(navController, Route.CREW_FLIGHT_CALENDAR, viewModel, connectivityStatus)
    }
  }
}
