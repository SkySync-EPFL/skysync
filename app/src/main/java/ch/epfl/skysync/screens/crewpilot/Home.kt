package ch.epfl.skysync.screens.crewpilot

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.UpcomingFlights
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.Purple40
import ch.epfl.skysync.viewmodel.FlightsViewModel

// Scaffold wrapper for the Home Screen
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavHostController, viewModel: FlightsViewModel) {
  val currentFlights by viewModel.currentFlights.collectAsStateWithLifecycle()
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      bottomBar = { BottomBar(navController) },
      floatingActionButton = {
        // Define the FloatingActionButton to create a flight
      },
      floatingActionButtonPosition = FabPosition.End,
  ) { padding ->
    UpcomingFlights(currentFlights, Purple40) { selectedFlight ->
      // Here is where you'd navigate to a new screen. For now, just log a message.
      Log.d("HomeScreen", "Navigating to FlightDetails with id $selectedFlight")

      navController.navigate(Route.FLIGHT_DETAILS + "/${selectedFlight}")
      // Example navigation call: navController.navigate("FlightDetails.id")
    }
  }
}

// Preview provider for the Home Screen
// @Composable
// @Preview
// fun HomeScreenPreview() {
//  // Preview navigation controller
//  val navController = rememberNavController()
//  // Preview of Home Screen
//  HomeScreen(navController = navController)
// }
