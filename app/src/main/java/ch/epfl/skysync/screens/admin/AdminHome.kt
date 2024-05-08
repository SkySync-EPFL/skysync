package ch.epfl.skysync.screens.admin

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.FlightsList
import ch.epfl.skysync.navigation.AdminBottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.lightOrange
import ch.epfl.skysync.viewmodel.FlightsViewModel

// Scaffold wrapper for the Home Screen
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AdminHomeScreen(navController: NavHostController, viewModel: FlightsViewModel) {
  val currentFlights by viewModel.currentFlights.collectAsStateWithLifecycle()
  // Display the Home Screen with the list of upcoming flights
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      bottomBar = { AdminBottomBar(navController = navController) },
      floatingActionButton = {
        // Define the FloatingActionButton to create a flight
        FloatingActionButton(
            modifier = Modifier.testTag("addFlightButton"),
            onClick = { navController.navigate(Route.ADD_FLIGHT) { launchSingleTop = true } },
            containerColor = lightOrange) {
              Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
      },
      floatingActionButtonPosition = FabPosition.End,
  ) { padding ->
    FlightsList(currentFlights, lightOrange, padding, "Upcoming flights") { selectedFlight ->
      Log.d("HomeScreen", "Navigating to FlightDetails with id $selectedFlight")
      navController.navigate(Route.ADMIN_FLIGHT_DETAILS + "/${selectedFlight}")
    }
  }
}
