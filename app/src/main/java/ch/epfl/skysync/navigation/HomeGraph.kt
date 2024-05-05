package ch.epfl.skysync.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.SnackbarManager
import ch.epfl.skysync.database.ListenerUpdate
import ch.epfl.skysync.models.message.Message
import ch.epfl.skysync.models.message.MessageGroup
import ch.epfl.skysync.screens.LoadingScreen
import ch.epfl.skysync.viewmodel.FlightsViewModel
import ch.epfl.skysync.viewmodel.TimerViewModel

/** Graph of the main screens of the app */
fun NavGraphBuilder.homeGraph(
    repository: Repository,
    navController: NavHostController,
    uid: String?,
    timer: TimerViewModel? = null
) {
  navigation(startDestination = Route.LOADING, route = Route.MAIN) {
    adminGraph(repository, navController, uid, timer)
    crewPilotGraph(repository, navController, uid, timer)
    composable(Route.LOADING) {
      val flightsOverviewViewModel = FlightsViewModel.createViewModel(repository, uid)
      flightsOverviewViewModel.refresh()
      LoadingScreen(navController = navController, viewModel = flightsOverviewViewModel)
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
