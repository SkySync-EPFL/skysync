package ch.epfl.skysync.navigation

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ch.epfl.skysync.Repository
import ch.epfl.skysync.screens.LoginScreen
import ch.epfl.skysync.viewmodel.FlightsViewModel
import ch.epfl.skysync.viewmodel.LocationViewModel
import ch.epfl.skysync.viewmodel.TimerViewModel

/** Graph of the whole navigation of the app */
@Composable
fun MainGraph(
    repository: Repository,
    navHostController: NavHostController,
    signInLauncher: ActivityResultLauncher<Intent>,
    uid: String?,
    inFlightsViewModel: LocationViewModel? = null
) {
  NavHost(
      navController = navHostController,
      startDestination = if (uid == null) Route.LOGIN else Route.MAIN) {
        homeGraph(repository, navHostController, uid, inFlightsViewModel)
        composable(Route.LOGIN) { LoginScreen(signInLauncher = signInLauncher) }
      }
}
