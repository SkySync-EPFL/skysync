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

/** Graph of the whole navigation of the app */
@Composable
fun MainGraph(
    repository: Repository,
    navHostController: NavHostController,
    signInLauncher: ActivityResultLauncher<Intent>,
    uid: String?
) {
  val flightsViewModel =
      FlightsViewModel.createViewModel(
          flightTable = repository.flightTable,
          balloonTable = repository.balloonTable,
          basketTable = repository.basketTable,
          flightTypeTable = repository.flightTypeTable,
          vehicleTable = repository.vehicleTable)
  NavHost(
      navController = navHostController,
      startDestination = if (uid == null) Route.LOGIN else Route.MAIN) {
        homeGraph(repository, navHostController, flightsViewModel, uid)
        composable(Route.LOGIN) { LoginScreen(signInLauncher = signInLauncher) }
      }
}
