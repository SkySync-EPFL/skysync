package ch.epfl.skysync.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import ch.epfl.skysync.Repository
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.screens.AddFlightScreen
import ch.epfl.skysync.screens.UserDetailsScreen
import ch.epfl.skysync.screens.admin.AddUserScreen
import ch.epfl.skysync.screens.admin.AdminChatScreen
import ch.epfl.skysync.screens.admin.AdminFlightDetailScreen
import ch.epfl.skysync.screens.admin.AdminHomeScreen
import ch.epfl.skysync.screens.admin.AdminStatsScreen
import ch.epfl.skysync.screens.admin.AdminTextScreen
import ch.epfl.skysync.screens.admin.ConfirmationScreen
import ch.epfl.skysync.screens.admin.ModifyFlightScreen
import ch.epfl.skysync.screens.admin.UserManagementScreen
import ch.epfl.skysync.viewmodel.ChatViewModel
import ch.epfl.skysync.viewmodel.FlightsViewModel
import ch.epfl.skysync.viewmodel.InFlightViewModel
import ch.epfl.skysync.viewmodel.MessageListenerSharedViewModel
import ch.epfl.skysync.viewmodel.UserManagementViewModel

fun NavGraphBuilder.adminGraph(
    repository: Repository,
    navController: NavHostController,
    uid: String?,
    inFlightViewModel: InFlightViewModel? = null
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
    composable(Route.STATS) { AdminStatsScreen(navController) }
    composable(Route.ADD_USER) {
      val userManagementViewModel = UserManagementViewModel.createViewModel(repository, uid)
      AddUserScreen(navController, userManagementViewModel)
    }
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
      val userManagementViewModel = UserManagementViewModel.createViewModel(repository, uid)
      userManagementViewModel.refresh()
      UserManagementScreen(navController = navController, userManagementViewModel)
    }
    composable(
        Route.ADMIN_USER_DETAILS + "/{User ID}",
        arguments = listOf(navArgument("User ID") { type = NavType.StringType })) { backStackEntry
          ->
          val selectedUserId = backStackEntry.arguments?.getString("User ID") ?: UNSET_ID
          val flightsViewModel = FlightsViewModel.createViewModel(repository, selectedUserId)
          UserDetailsScreen(navController, flightsViewModel)
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
