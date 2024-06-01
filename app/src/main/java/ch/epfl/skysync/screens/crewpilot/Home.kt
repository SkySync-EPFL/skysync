package ch.epfl.skysync.screens.crewpilot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.FlightsList
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.ScreenTitles
import ch.epfl.skysync.ui.theme.getThemeColor
import ch.epfl.skysync.viewmodel.FlightsViewModel

/** represents the home screen of the crew/pilot */
@Composable
fun HomeScreen(navController: NavHostController, viewModel: FlightsViewModel) {
  val currentFlights by viewModel.currentFlights.collectAsStateWithLifecycle()
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      bottomBar = { BottomBar(navController) },
  ) { padding ->
    FlightsList(currentFlights, getThemeColor(isAdmin = false), padding, ScreenTitles.HOME) {
        selectedFlight ->
      navController.navigate(Route.CREW_FLIGHT_DETAILS + "/${selectedFlight}")
    }
  }
}
