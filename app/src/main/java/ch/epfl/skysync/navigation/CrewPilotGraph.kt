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
import ch.epfl.skysync.screens.crewpilot.LaunchFlight
import ch.epfl.skysync.screens.crewpilot.TextScreen
import ch.epfl.skysync.screens.reports.CrewReportScreen
import ch.epfl.skysync.screens.reports.PilotReportScreen
import ch.epfl.skysync.viewmodel.ChatViewModel
import ch.epfl.skysync.viewmodel.FlightsViewModel
import ch.epfl.skysync.viewmodel.InFlightViewModel
import ch.epfl.skysync.viewmodel.MessageListenerViewModel
import java.time.LocalDate
import java.util.Date

fun NavGraphBuilder.crewPilotGraph(
    repository: Repository,
    navController: NavHostController,
    uid: String?,
    inFlightViewModel: InFlightViewModel? = null,
    messageListenerViewModel: MessageListenerViewModel? = null,
    connectivityStatus: ConnectivityStatus
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
    composable(Route.FLIGHT) {
      FlightScreen(navController, inFlightViewModel!!, uid!!)
    }
    composable(Route.PILOT_REPORT) {
      // TODO remove when done with viewModel
      val pilot =
          Pilot("testID", "John", "Doe", "", setOf(RoleType.PILOT), BalloonQualification.MEDIUM)
      val finishedFlight =
          FinishedFlight(
              UNSET_ID,
              0,
              Team(Role.initRoles(BASE_ROLES)),
              FlightType.DISCOVERY,
              Balloon("Balloon 1", BalloonQualification.MEDIUM),
              Basket("Basket 1", true),
              LocalDate.now(),
              TimeSlot.AM,
              listOf(),
              takeOffTime = Date(),
              takeOffLocation =
                  LocationPoint(time = 0, latitude = 0.0, longitude = 0.0, name = "test1"),
              landingTime = Date(),
              landingLocation =
                  LocationPoint(time = 50, latitude = 1.0, longitude = 1.0, name = "test2"),
              flightTime = 0)

      PilotReportScreen(finishedFlight, navController, pilot)
    }
    composable(Route.CREW_REPORT) {
      // TODO remove when done with viewModel
      val crew =
          Crew(
              "testID",
              "John",
              "Doe",
              "",
              setOf(RoleType.PILOT),
          )
      val finishedFlight =
          FinishedFlight(
              UNSET_ID,
              0,
              Team(Role.initRoles(BASE_ROLES)),
              FlightType.DISCOVERY,
              Balloon("Balloon 1", BalloonQualification.MEDIUM),
              Basket("Basket 1", true),
              LocalDate.now(),
              TimeSlot.AM,
              listOf(Vehicle("vehicle1"), Vehicle("vehicle2")),
              takeOffTime = Date(),
              takeOffLocation =
                  LocationPoint(time = 0, latitude = 0.0, longitude = 0.0, name = "test1"),
              landingTime = Date(),
              landingLocation =
                  LocationPoint(time = 50, latitude = 1.0, longitude = 1.0, name = "test2"),
              flightTime = 0)
      CrewReportScreen(navController, finishedFlight, crew)
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
  }
}
