package ch.epfl.skysync.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.ConnectivityStatus
import ch.epfl.skysync.components.SnackbarManager
import ch.epfl.skysync.database.ListenerUpdate
import ch.epfl.skysync.models.message.Message
import ch.epfl.skysync.models.message.MessageGroup
import ch.epfl.skysync.screens.LoadingScreen
import ch.epfl.skysync.viewmodel.FlightsViewModel
import ch.epfl.skysync.viewmodel.InFlightViewModel
import ch.epfl.skysync.viewmodel.MessageListenerViewModel

/**
 * The home graph
 *
 * @param repository The repository
 * @param navController The navigation controller
 * @param uid The user ID
 * @param inFlightViewModel The in flight view model
 * @param messageListenerViewModel The message listener view model
 * @param connectivityStatus The connectivity status
 */
fun NavGraphBuilder.homeGraph(
    repository: Repository,
    navController: NavHostController,
    uid: String?,
    inFlightViewModel: InFlightViewModel? = null,
    messageListenerViewModel: MessageListenerViewModel? = null,
    connectivityStatus: ConnectivityStatus
) {
  navigation(startDestination = Route.LOADING, route = Route.MAIN) {
    adminGraph(
        repository,
        navController,
        uid,
        inFlightViewModel,
        messageListenerViewModel,
        connectivityStatus)
    crewPilotGraph(
        repository,
        navController,
        uid,
        inFlightViewModel,
        messageListenerViewModel,
        connectivityStatus)
    composable(Route.LOADING) {
      val flightsOverviewViewModel = FlightsViewModel.createViewModel(repository, uid)
      flightsOverviewViewModel.refresh()
      LoadingScreen(navController = navController, viewModel = flightsOverviewViewModel)
    }
  }
}

/**
 * Callback executed when a message update is triggered anywhere in the app. Displays a snackbar
 * message
 *
 * (Should be refactored to a better solution later)
 */
fun onMessageUpdate(group: MessageGroup, update: ListenerUpdate<Message>) {
  if (update.isFirstUpdate) return
  val message = update.adds.firstOrNull() ?: return
  SnackbarManager.showMessage("(${group.name}) ${message.user.firstname}: ${message.content}")
}
