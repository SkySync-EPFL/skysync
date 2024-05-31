package ch.epfl.skysync.screens.crewpilot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.FlightsList
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.Purple40
import ch.epfl.skysync.viewmodel.FinishedFlightsViewModel

/**
 * The screen for the stats for the crew/pilot user
 *
 * @param navController The navigation controller
 * @param viewModel The view model
 */
@Composable
fun StatsScreen(navController: NavHostController, viewModel: FinishedFlightsViewModel) {
  val finishedFlights by viewModel.currentFlights.collectAsStateWithLifecycle()
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      bottomBar = { BottomBar(navController) },
      floatingActionButton = {},
      floatingActionButtonPosition = FabPosition.End,
  ) { padding ->
    FlightsList(finishedFlights, Purple40, padding, "Flights History") { selectedFlight ->
      navController.navigate(Route.CREW_FLIGHT_DETAILS + "/${selectedFlight}")
    }
  }
}
