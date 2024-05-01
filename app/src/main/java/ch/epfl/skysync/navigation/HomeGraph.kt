package ch.epfl.skysync.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.SnackbarManager
import ch.epfl.skysync.database.ListenerUpdate
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.message.Message
import ch.epfl.skysync.models.message.MessageGroup
import ch.epfl.skysync.screens.AddFlightScreen
import ch.epfl.skysync.screens.AddUserScreen
import ch.epfl.skysync.screens.ChatScreen
import ch.epfl.skysync.screens.FlightScreen
import ch.epfl.skysync.screens.HomeScreen
import ch.epfl.skysync.screens.ModifyFlightScreen
import ch.epfl.skysync.screens.TextScreen
import ch.epfl.skysync.screens.confirmationScreen
import ch.epfl.skysync.screens.flightDetail.FlightDetailScreen
import ch.epfl.skysync.viewmodel.ChatViewModel
import ch.epfl.skysync.viewmodel.FlightsViewModel
import ch.epfl.skysync.viewmodel.MessageListenerSharedViewModel

/** Graph of the main screens of the app */
fun NavGraphBuilder.homeGraph(
    repository: Repository,
    navController: NavHostController,
    uid: String?
) {
  navigation(startDestination = Route.HOME, route = Route.MAIN) {
    personalCalendar(repository, navController, uid)
    composable(Route.CHAT) { entry ->
      val messageListenerSharedViewModel =
          entry.sharedViewModel<MessageListenerSharedViewModel>(
              navController,
          )
      val chatViewModel =
          ChatViewModel.createViewModel(uid!!, messageListenerSharedViewModel, repository)
      ChatScreen(navController, chatViewModel)
    }
    composable(Route.FLIGHT) { FlightScreen(navController) }
    composable(Route.HOME) { entry ->

      // get the MessageListenerSharedViewModel here so that it gets
      // instantiated
      val messageListenerSharedViewModel =
          entry.sharedViewModel<MessageListenerSharedViewModel>(
              navController,
          )
      messageListenerSharedViewModel.init(uid!!, repository) { group, update ->
        onMessageUpdate(group, update)
      }

      val flightsViewModel = FlightsViewModel.createViewModel(repository, uid)
        flightsViewModel.refresh()
      HomeScreen(navController, flightsViewModel)
    }
    composable(
        route = Route.FLIGHT_DETAILS + "/{Flight ID}",
        arguments = listOf(navArgument("Flight ID") { type = NavType.StringType })) { backStackEntry
          ->
          val flightId = backStackEntry.arguments?.getString("Flight ID") ?: UNSET_ID
          val flightsViewModel = FlightsViewModel.createViewModel(repository, uid)
          FlightDetailScreen(
              navController = navController, flightId = flightId, viewModel = flightsViewModel)
        }
    composable(Route.ADD_FLIGHT) {
      val flightsViewModel = FlightsViewModel.createViewModel(repository, uid)
      AddFlightScreen(navController, flightsViewModel)
    }
    composable(
        Route.CONFIRM_FLIGHT + "/{Flight ID}",
        arguments = listOf(navArgument("Flight ID") { type = NavType.StringType })) { backStackEntry
          ->
          val flightId = backStackEntry.arguments?.getString("Flight ID") ?: UNSET_ID
          val flightsViewModel = FlightsViewModel.createViewModel(repository, uid)
          confirmationScreen(navController, flightId, flightsViewModel)
        }

    composable(
        route = Route.MODIFY_FLIGHT + "/{Flight ID}",
        arguments = listOf(navArgument("Flight ID") { type = NavType.StringType })) { backStackEntry
          ->
          val flightId = backStackEntry.arguments?.getString("Flight ID") ?: UNSET_ID
          val flightsViewModel = FlightsViewModel.createViewModel(repository, uid)
          ModifyFlightScreen(navController, flightsViewModel, flightId)
        }
    composable(Route.ADD_USER) { AddUserScreen(navController = navController) }
    composable(
        Route.TEXT + "/{Group ID}",
        arguments = listOf(navArgument("Group ID") { type = NavType.StringType })) { entry ->
          val messageListenerSharedViewModel =
              entry.sharedViewModel<MessageListenerSharedViewModel>(
                  navController,
              )

          val chatViewModel =
              ChatViewModel.createViewModel(uid!!, messageListenerSharedViewModel, repository)
          val groupId = entry.arguments?.getString("Group ID")
          if (groupId == null) {
            navController.navigate(Route.HOME)
            return@composable
          }
          TextScreen(navController, groupId, chatViewModel)
        }
  }
}

/**
 * Callback executed when a message update is triggered anywhere in the app. Display a snackbar
 * message
 *
 * (Should be refactored to a better solution later)
 */
fun onMessageUpdate(group: MessageGroup, update: ListenerUpdate<Message>) {
  if (update.isFirstUpdate) return
  val message = update.adds.firstOrNull() ?: return
  SnackbarManager.showMessage("(${group.name}) ${message.user.firstname}: ${message.content}")
}

/**
 * Source:
 * https://github.com/philipplackner/SharingDataBetweenScreens/blob/master/app/src/main/java/com/plcoding/sharingdataprep/content/2-SharedViewModel.kt
 */
@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavHostController,
): T {
  val navGraphRoute = destination.parent?.route ?: return viewModel()
  val parentEntry = remember(this) { navController.getBackStackEntry(navGraphRoute) }
  return viewModel(parentEntry)
}
