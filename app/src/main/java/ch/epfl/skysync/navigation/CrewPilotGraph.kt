package ch.epfl.skysync.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import ch.epfl.skysync.Repository
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.BASE_ROLES
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.location.LocationPoint
import ch.epfl.skysync.models.user.Crew
import ch.epfl.skysync.models.user.Pilot
import ch.epfl.skysync.screens.crewpilot.ChatScreen
import ch.epfl.skysync.screens.crewpilot.FlightDetailScreen
import ch.epfl.skysync.screens.crewpilot.FlightScreen
import ch.epfl.skysync.screens.crewpilot.HomeScreen
import ch.epfl.skysync.screens.crewpilot.TextScreen
import ch.epfl.skysync.screens.reports.CrewReportScreen
import ch.epfl.skysync.screens.reports.PilotReportScreen
import ch.epfl.skysync.viewmodel.ChatViewModel
import ch.epfl.skysync.viewmodel.FinishedFlightsViewModel
import ch.epfl.skysync.viewmodel.FlightsViewModel
import ch.epfl.skysync.viewmodel.InFlightViewModel
import ch.epfl.skysync.viewmodel.MessageListenerSharedViewModel

fun NavGraphBuilder.crewPilotGraph(
    repository: Repository,
    navController: NavHostController,
    uid: String?,
    inFlightViewModel: InFlightViewModel? = null
) {
  navigation(startDestination = Route.CREW_HOME, route = Route.CREW_PILOT) {
    personalCalendar(repository, navController, uid)
    composable(
        route = Route.CREW_FLIGHT_DETAILS + "/{Flight ID}",
        arguments = listOf(navArgument("Flight ID") { type = NavType.StringType })) { backStackEntry
          ->
          val flightId = backStackEntry.arguments?.getString("Flight ID") ?: UNSET_ID
          val flightsViewModel = FlightsViewModel.createViewModel(repository, uid)
          FlightDetailScreen(
              navController = navController, flightId = flightId, viewModel = flightsViewModel)
        }
    composable(
        Route.CREW_TEXT + "/{Group ID}",
        arguments = listOf(navArgument("Group ID") { type = NavType.StringType })) { entry ->
          val messageListenerSharedViewModel =
              entry.sharedViewModel<MessageListenerSharedViewModel>(
                  navController,
              )

          val chatViewModel =
              ChatViewModel.createViewModel(uid!!, messageListenerSharedViewModel, repository)
          val groupId = entry.arguments?.getString("Group ID")
          if (groupId == null) {
            navController.navigate(Route.CREW_HOME)
            return@composable
          }
          TextScreen(navController, groupId, chatViewModel)
        }
    composable(Route.CREW_HOME) { entry ->

      // initiate in flight view model here, so that we can notify
      // the user when a flight is started by someone else
      inFlightViewModel!!.init(uid!!)

      // get the MessageListenerSharedViewModel here so that it gets
      // instantiated
      val messageListenerSharedViewModel =
          entry.sharedViewModel<MessageListenerSharedViewModel>(
              navController,
          )
      messageListenerSharedViewModel.init(uid!!, repository) { group, update ->
        onMessageUpdate(group, update)
      }

      val flightsOverviewViewModel = FlightsViewModel.createViewModel(repository, uid)
      flightsOverviewViewModel.refresh()
      HomeScreen(navController, flightsOverviewViewModel)
    }
    composable(Route.CREW_CHAT) { entry ->
      val messageListenerSharedViewModel =
          entry.sharedViewModel<MessageListenerSharedViewModel>(
              navController,
          )
      val chatViewModel =
          ChatViewModel.createViewModel(uid!!, messageListenerSharedViewModel, repository)
      ChatScreen(navController, chatViewModel)
    }
    composable(Route.FLIGHT) { FlightScreen(navController, inFlightViewModel!!, uid!!) }
    composable(Route.PILOT_REPORT) {
      val finishedFlightsViewModel = FinishedFlightsViewModel.createViewModel(repository, uid)

      PilotReportScreen(navController, finishedFlightsViewModel)
    }
    composable(Route.CREW_REPORT) {
      val finishedFlightsViewModel = FinishedFlightsViewModel.createViewModel(repository, uid)
      CrewReportScreen(navController, finishedFlightsViewModel)
    }
  }
}
