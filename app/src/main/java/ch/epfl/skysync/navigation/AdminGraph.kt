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
import ch.epfl.skysync.screens.UserDetailsScreen
import ch.epfl.skysync.screens.admin.AddFlightScreen
import ch.epfl.skysync.screens.admin.AddUserScreen
import ch.epfl.skysync.screens.admin.AdminChatScreen
import ch.epfl.skysync.screens.admin.AdminFlightDetailScreen
import ch.epfl.skysync.screens.admin.AdminFlightScreen
import ch.epfl.skysync.screens.admin.AdminHomeScreen
import ch.epfl.skysync.screens.admin.AdminStatsScreen
import ch.epfl.skysync.screens.admin.AdminTextScreen
import ch.epfl.skysync.screens.admin.ConfirmationScreen
import ch.epfl.skysync.screens.admin.ModifyFlightScreen
import ch.epfl.skysync.screens.admin.UserManagementScreen
import ch.epfl.skysync.screens.reports.ReportDetailsScreen
import ch.epfl.skysync.viewmodel.ChatViewModel
import ch.epfl.skysync.viewmodel.FinishedFlightsViewModel
import ch.epfl.skysync.viewmodel.FlightsViewModel
import ch.epfl.skysync.viewmodel.InFlightViewModel
import ch.epfl.skysync.viewmodel.MessageListenerViewModel
import ch.epfl.skysync.viewmodel.UserManagementViewModel

/**
 * The admin graph
 *
 * @param repository The repository
 * @param navController The navigation controller
 * @param uid The user ID
 * @param inFlightViewModel The in flight view model
 * @param messageListenerViewModel The message listener view model
 * @param connectivityStatus The connectivity status
 */
fun NavGraphBuilder.adminGraph(
    repository: Repository,
    navController: NavHostController,
    uid: String?,
    inFlightViewModel: InFlightViewModel? = null,
    messageListenerViewModel: MessageListenerViewModel? = null,
    connectivityStatus: ConnectivityStatus
) {
  navigation(startDestination = Route.ADMIN_HOME, route = Route.ADMIN) {
    adminpersonalCalendar(repository, navController, uid, connectivityStatus)
    composable(Route.ADMIN_HOME) {
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
      AdminHomeScreen(navController, flightsOverviewViewModel, connectivityStatus)
    }

    composable(Route.ADD_FLIGHT) {
      val flightsViewModel = FlightsViewModel.createViewModel(repository, uid)
      AddFlightScreen(navController, flightsViewModel)
    }
    composable(Route.ADMIN_STATS) {
      val viewModel =
          FinishedFlightsViewModel.createViewModel(repository = repository, userId = uid!!)
      viewModel.refresh()
      AdminStatsScreen(navController, viewModel)
    }
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
      UserManagementScreen(
          navController = navController, userManagementViewModel, connectivityStatus)
    }
    composable(
        Route.ADMIN_USER_DETAILS + "/{User ID}",
        arguments = listOf(navArgument("User ID") { type = NavType.StringType })) { backStackEntry
          ->
          val selectedUserId = backStackEntry.arguments?.getString("User ID") ?: UNSET_ID
          val flightsViewModel = FlightsViewModel.createViewModel(repository, selectedUserId)
          UserDetailsScreen(navController, flightsViewModel)
        }
    composable(Route.ADMIN_CHAT) {
      val chatViewModel =
          ChatViewModel.createViewModel(uid!!, messageListenerViewModel!!, repository)
      AdminChatScreen(navController, chatViewModel)
    }
    composable(
        Route.ADMIN_TEXT + "/{Group ID}",
        arguments = listOf(navArgument("Group ID") { type = NavType.StringType })) { entry ->
          val chatViewModel =
              ChatViewModel.createViewModel(uid!!, messageListenerViewModel!!, repository)
          val groupId = entry.arguments?.getString("Group ID")
          if (groupId == null) {
            navController.navigate(Route.ADMIN_HOME)
            return@composable
          }
          AdminTextScreen(navController, groupId, chatViewModel, connectivityStatus)
        }
    composable(
        Route.ADMIN_FLIGHT_DETAILS + "/{Flight ID}",
        arguments = listOf(navArgument("Flight ID") { type = NavType.StringType })) { backStackEntry
          ->
          val flightId = backStackEntry.arguments?.getString("Flight ID") ?: UNSET_ID
          val flightsViewModel = FlightsViewModel.createViewModel(repository, uid)
          inFlightViewModel!!.init(uid!!)
          AdminFlightDetailScreen(
              navController = navController,
              flightId = flightId,
              viewModel = flightsViewModel,
              inFlightViewModel = inFlightViewModel,
              connectivityStatus)
        }
    composable(
        Route.REPORT + "/{flight ID}",
        arguments = listOf(navArgument("flight ID") { type = NavType.StringType })) { entry ->
          val flightId = entry.arguments?.getString("flight ID") ?: UNSET_ID
          val finishedFlightsViewModel = FinishedFlightsViewModel.createViewModel(repository, uid!!)
          finishedFlightsViewModel.refresh()
          finishedFlightsViewModel.getAllReports(flightId)
          ReportDetailsScreen(flightId, finishedFlightsViewModel, true, uid, navController)
        }
    composable(Route.ADMIN_FLIGHT) { AdminFlightScreen(navController, inFlightViewModel!!) }
  }
}
