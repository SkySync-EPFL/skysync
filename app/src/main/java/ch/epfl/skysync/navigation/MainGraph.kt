package ch.epfl.skysync.navigation

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.ConnectivityStatus
import ch.epfl.skysync.screens.LoginScreen
import ch.epfl.skysync.viewmodel.InFlightViewModel
import ch.epfl.skysync.viewmodel.MessageListenerViewModel
import ch.epfl.skysync.viewmodel.UserGlobalViewModel

/** Graph of the whole navigation of the app */
@Composable
fun MainGraph(
    repository: Repository,
    navHostController: NavHostController,
    signInLauncher: ActivityResultLauncher<Intent>,
    inFlightsViewModel: InFlightViewModel? = null,
    messageListenerViewModel: MessageListenerViewModel? = null,
    userGlobalViewModel: UserGlobalViewModel,
) {
  val user by userGlobalViewModel.user.collectAsStateWithLifecycle()
  val context = LocalContext.current
  val connectivityStatus = remember { ConnectivityStatus(context) }
  NavHost(
      navController = navHostController,
      startDestination = if (user == null) Route.LOGIN else Route.MAIN) {
        // only pass the uid for the moment as passing a user object
        // poses the question of how and when to refresh it
        // and we would need to change the structure of all view models and tests
        homeGraph(
            repository,
            navHostController,
            user?.id,
            inFlightsViewModel,
            messageListenerViewModel,
            connectivityStatus)
        composable(Route.LOGIN) { LoginScreen(userGlobalViewModel, signInLauncher) }
      }
}
