package ch.epfl.skysync.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import ch.epfl.skysync.Repository
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.user.Pilot
import ch.epfl.skysync.screens.AddFlightScreen
import ch.epfl.skysync.screens.admin.AddUserScreen
import ch.epfl.skysync.screens.admin.AdminChatScreen
import ch.epfl.skysync.screens.admin.AdminFlightDetailScreen
import ch.epfl.skysync.screens.admin.AdminHomeScreen
import ch.epfl.skysync.screens.admin.AdminTextScreen
import ch.epfl.skysync.screens.admin.ConfirmationScreen
import ch.epfl.skysync.screens.admin.ModifyFlightScreen
import ch.epfl.skysync.screens.admin.UserManagementScreen
import ch.epfl.skysync.viewmodel.ChatViewModel
import ch.epfl.skysync.viewmodel.FlightsViewModel
import ch.epfl.skysync.viewmodel.LocationViewModel
import ch.epfl.skysync.viewmodel.MessageListenerSharedViewModel

fun NavGraphBuilder.adminGraph(
    repository: Repository,
    navController: NavHostController,
    uid: String?,
    locationViewModel: LocationViewModel? = null
) {
  navigation(startDestination = Route.ADMIN_HOME, route = Route.ADMIN) {
    adminpersonalCalendar(repository, navController, uid)
    composable(Route.ADMIN_HOME) {
      val flightsOverviewViewModel = FlightsViewModel.createViewModel(repository, uid)
      flightsOverviewViewModel.refresh()
      AdminHomeScreen(navController, flightsOverviewViewModel)
    }
    composable(Route.ADD_FLIGHT) {
      val flightsViewModel = FlightsViewModel.createViewModel(repository, uid)
      AddFlightScreen(navController, flightsViewModel)
    }
    composable(Route.STATS) {
      // TODO
    }
    composable(Route.ADD_USER) { AddUserScreen(navController) }
    composable(
        Route.CONFIRM_FLIGHT + "/{Flight ID}",
        arguments = listOf(navArgument("Flight ID") { type = NavType.StringType })) { backStackEntry
          ->
          val flightId = backStackEntry.arguments?.getString("Flight ID") ?: UNSET_ID
          val flightsViewModel = FlightsViewModel.createViewModel(repository, uid)
          ConfirmationScreen(navController, flightId, flightsViewModel)
        }
    composable(
        route = Route.MODIFY_FLIGHT + "/{Flight ID}",
        arguments = listOf(navArgument("Flight ID") { type = NavType.StringType })) { backStackEntry
          ->
          val flightId = backStackEntry.arguments?.getString("Flight ID") ?: UNSET_ID
          val flightsViewModel = FlightsViewModel.createViewModel(repository, uid)
          ModifyFlightScreen(navController, flightsViewModel, flightId)
        }
    composable(Route.USER) {
      val users =
          listOf(
              Pilot(
                  "testID",
                  "John",
                  "Doe",
                  "john.doe@gmail.com",
                  AvailabilityCalendar(mutableListOf()),
                  FlightGroupCalendar(),
                  setOf(RoleType.PILOT),
                  BalloonQualification.MEDIUM))
      UserManagementScreen(navController = navController, users = users)
    }
    composable(Route.ADMIN_CHAT) { entry ->
      val messageListenerSharedViewModel =
          entry.sharedViewModel<MessageListenerSharedViewModel>(
              navController,
          )
      val chatViewModel =
          ChatViewModel.createViewModel(uid!!, messageListenerSharedViewModel, repository)
      AdminChatScreen(navController, chatViewModel)
    }
    composable(
        Route.ADMIN_TEXT + "/{Group ID}",
        arguments = listOf(navArgument("Group ID") { type = NavType.StringType })) { entry ->
          val messageListenerSharedViewModel =
              entry.sharedViewModel<MessageListenerSharedViewModel>(
                  navController,
              )

          val chatViewModel =
              ChatViewModel.createViewModel(uid!!, messageListenerSharedViewModel, repository)
          val groupId = entry.arguments?.getString("Group ID")
          if (groupId == null) {
            navController.navigate(Route.ADMIN_HOME)
            return@composable
          }
          AdminTextScreen(navController, groupId, chatViewModel)
        }
    composable(
        Route.ADMIN_FLIGHT_DETAILS + "/{Flight ID}",
        arguments = listOf(navArgument("Flight ID") { type = NavType.StringType })) { backStackEntry
          ->
          val flightId = backStackEntry.arguments?.getString("Flight ID") ?: UNSET_ID
          val flightsViewModel = FlightsViewModel.createViewModel(repository, uid)
          AdminFlightDetailScreen(
              navController = navController, flightId = flightId, viewModel = flightsViewModel)
        }
  }
}
