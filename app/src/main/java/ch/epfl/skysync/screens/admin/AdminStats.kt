package ch.epfl.skysync.screens.admin

import android.util.Log
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
import ch.epfl.skysync.ui.theme.lightOrange
import ch.epfl.skysync.viewmodel.FinishedFlightsViewModel

@Composable
fun AdminStatsScreen(navController: NavHostController, viewModel: FinishedFlightsViewModel) {
  val finishedFlights by viewModel.currentFlights.collectAsStateWithLifecycle()
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      bottomBar = { BottomBar(navController) },
      floatingActionButton = {},
      floatingActionButtonPosition = FabPosition.End,
  ) { padding ->
    FlightsList(finishedFlights, lightOrange, padding, "Flights History") { selectedFlight ->
      Log.d("StatsScreen", "Navigating to FinishFlightDetails with id $selectedFlight")
      navController.navigate(Route.ADMIN_FLIGHT_DETAILS + "/${selectedFlight}")
    }
  }
}
