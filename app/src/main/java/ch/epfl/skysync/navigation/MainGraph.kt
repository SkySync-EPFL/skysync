package ch.epfl.skysync.navigation

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ch.epfl.skysync.Repository
import ch.epfl.skysync.screens.LoginScreen
import ch.epfl.skysync.viewmodel.UserGlobalViewModel
import ch.epfl.skysync.viewmodel.LocationViewModel

/** Graph of the whole navigation of the app */
@Composable
fun MainGraph(
    repository: Repository,
    navHostController: NavHostController,
    signInLauncher: ActivityResultLauncher<Intent>,
    uid: String?,
    inFlightsViewModel: LocationViewModel? = null
    userGlobalViewModel: UserGlobalViewModel,
) {
  val user by userGlobalViewModel.user.collectAsStateWithLifecycle()
  NavHost(
      navController = navHostController,
      startDestination = if (user == null) Route.LOGIN else Route.MAIN) {
        // only pass the uid for the moment as passing a user object
        // poses the question of how and when to refresh it
        // and we would need to change the structure of all view models and tests
        homeGraph(repository, navHostController, user?.id, timer)
        composable(Route.LOGIN) { LoginScreen(userGlobalViewModel, signInLauncher) }
      startDestination = if (uid == null) Route.LOGIN else Route.MAIN) {
        homeGraph(repository, navHostController, uid, inFlightsViewModel)
        composable(Route.LOGIN) { LoginScreen(signInLauncher = signInLauncher) }
      }
}
