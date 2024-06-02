package ch.epfl.skysync.screens.admin

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.FlightsList
import ch.epfl.skysync.navigation.AdminBottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.ScreenTitles
import ch.epfl.skysync.ui.theme.getThemeColor
import ch.epfl.skysync.viewmodel.FinishedFlightsViewModel

/**
 * The screen for the admin stats
 *
 * @param navController The navigation controller
 * @param viewModel The view model
 */
@Composable
fun AdminStatsScreen(navController: NavHostController, viewModel: FinishedFlightsViewModel) {
  val finishedFlights by viewModel.currentFlights.collectAsStateWithLifecycle()
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      bottomBar = { AdminBottomBar(navController) },
      floatingActionButton = {},
      floatingActionButtonPosition = FabPosition.End,
  ) { padding ->
    FlightsList(finishedFlights, getThemeColor(isAdmin = true), padding, ScreenTitles.HISTORY) {
        selectedFlight ->
      navController.navigate(Route.ADMIN_FLIGHT_DETAILS + "/${selectedFlight}")
    }
  }
}
