package ch.epfl.skysync.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.ConnectivityStatus
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.screens.crewpilot.ChatScreen
import ch.epfl.skysync.screens.crewpilot.CrewFlightDetailScreen
import ch.epfl.skysync.screens.crewpilot.FlightScreen
import ch.epfl.skysync.screens.crewpilot.HomeScreen
import ch.epfl.skysync.screens.crewpilot.LaunchFlight
import ch.epfl.skysync.screens.crewpilot.StatsScreen
import ch.epfl.skysync.screens.crewpilot.TextScreen
import ch.epfl.skysync.screens.reports.CrewReportScreen
import ch.epfl.skysync.screens.reports.PilotReportScreen
import ch.epfl.skysync.screens.reports.ReportDetailsScreen
import ch.epfl.skysync.viewmodel.ChatViewModel
import ch.epfl.skysync.viewmodel.FinishedFlightsViewModel
import ch.epfl.skysync.viewmodel.FlightsViewModel
import ch.epfl.skysync.viewmodel.InFlightViewModel
import ch.epfl.skysync.viewmodel.MessageListenerViewModel

/**
 * The crew/pilot graph
 *
 * @param repository The repository
 * @param navController The navigation controller
 * @param uid The user ID
 * @param inFlightViewModel The in flight view model
 * @param messageListenerViewModel The message listener view model
 * @param connectivityStatus The connectivity status
 */
fun NavGraphBuilder.crewPilotGraph(
    repository: Repository,
    navController: NavHostController,
    uid: String?,
    inFlightViewModel: InFlightViewModel? = null,
    messageListenerViewModel: MessageListenerViewModel? = null,
    connectivityStatus: ConnectivityStatus
) {
  navigation(startDestination = Route.CREW_HOME, route = Route.CREW_PILOT) {
    personalCalendar(repository, navController, uid, connectivityStatus)
    composable(
        route = Route.CREW_FLIGHT_DETAILS + "/{Flight ID}",
        arguments = listOf(navArgument("Flight ID") { type = NavType.StringType })) { backStackEntry
          ->
          val flightId = backStackEntry.arguments?.getString("Flight ID") ?: UNSET_ID
          val flightsViewModel = FlightsViewModel.createViewModel(repository, uid)
          inFlightViewModel!!.init(uid!!)

          CrewFlightDetailScreen(
              navController = navController,
              flightId = flightId,
              viewModel = flightsViewModel,
              inFlightViewModel = inFlightViewModel)
        }
    composable(
        Route.CREW_TEXT + "/{Group ID}",
        arguments = listOf(navArgument("Group ID") { type = NavType.StringType })) { entry ->
          val chatViewModel =
              ChatViewModel.createViewModel(uid!!, messageListenerViewModel!!, repository)

          val groupId = entry.arguments?.getString("Group ID")
          if (groupId == null) {
            navController.navigate(Route.CREW_HOME)
            return@composable
          }
          TextScreen(navController, groupId, chatViewModel, connectivityStatus)
        }
    composable(Route.CREW_HOME) { entry ->

      // initiate in flight view model here, so that we can notify
      // the user when a flight is started by someone else
      inFlightViewModel!!.init(uid!!)

      // get the MessageListenerSharedViewModel here so that it gets
      // instantiated
      messageListenerViewModel!!.init(uid, repository) { group, update ->
        onMessageUpdate(group, update)
      }

      val flightsOverviewViewModel = FlightsViewModel.createViewModel(repository, uid)
      flightsOverviewViewModel.refresh()
      HomeScreen(navController, flightsOverviewViewModel)
    }
    composable(Route.CREW_CHAT) {
      val chatViewModel =
          ChatViewModel.createViewModel(uid!!, messageListenerViewModel!!, repository)
      ChatScreen(navController, chatViewModel)
    }
    composable(
        Route.REPORT + "/{flight ID}",
        arguments = listOf(navArgument("flight ID") { type = NavType.StringType })) { entry ->
          val flightId = entry.arguments?.getString("flight ID") ?: UNSET_ID
          val finishedFlightsViewModel = FinishedFlightsViewModel.createViewModel(repository, uid!!)
          finishedFlightsViewModel.refresh()
          finishedFlightsViewModel.getAllReports(flightId)
          ReportDetailsScreen(flightId, finishedFlightsViewModel, false, uid, navController)
        }
    composable(Route.FLIGHT) { FlightScreen(navController, inFlightViewModel!!, uid!!) }
    composable(
        Route.PILOT_REPORT + "/{flight ID}",
        arguments = listOf(navArgument("flight ID") { type = NavType.StringType })) { entry ->
          val flightId = entry.arguments?.getString("flight ID") ?: UNSET_ID
          val finishedFlightsViewModel = FinishedFlightsViewModel.createViewModel(repository, uid!!)
          finishedFlightsViewModel.refresh()
          PilotReportScreen(navController, finishedFlightsViewModel, flightId)
        }
    composable(
        Route.CREW_REPORT + "/{flight ID}",
        arguments = listOf(navArgument("flight ID") { type = NavType.StringType })) { entry ->
          val flightId = entry.arguments?.getString("flight ID") ?: UNSET_ID
          val finishedFlightsViewModel = FinishedFlightsViewModel.createViewModel(repository, uid!!)
          finishedFlightsViewModel.refresh()
          CrewReportScreen(navController, finishedFlightsViewModel, flightId)
        }
    composable(Route.LAUNCH_FLIGHT) {
      val viewModel = FlightsViewModel.createViewModel(repository, uid)
      viewModel.refresh()
      LaunchFlight(
          navController = navController,
          flightViewModel = viewModel,
          inFlightViewModel = inFlightViewModel!!,
          connectivityStatus)
    }
    composable(Route.STATS) {
      val viewModel =
          FinishedFlightsViewModel.createViewModel(repository = repository, userId = uid!!)
      viewModel.refresh()
      StatsScreen(navController, viewModel)
    }
  }
}
