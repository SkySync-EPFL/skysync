package ch.epfl.skysync.screens

import android.annotation.SuppressLint
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
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.models.user.Admin
import ch.epfl.skysync.navigation.AdminBottomBar
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.Purple40
import ch.epfl.skysync.ui.theme.lightOrange
import ch.epfl.skysync.viewmodel.FlightsViewModel

// Scaffold wrapper for the Home Screen
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavHostController, viewModel: FlightsViewModel) {
  val currentFlights by viewModel.currentFlights.collectAsStateWithLifecycle()
  val user by viewModel.currentUser.collectAsStateWithLifecycle()

  if (user == null) {
    LoadingComponent(isLoading = true, onRefresh = { /*TODO*/}) {}
  } else {
    if (user is Admin) {
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
                  Icon(
                      imageVector = Icons.Default.Add,
                      contentDescription = "Add",
                      tint = Color.White)
                }
          },
          floatingActionButtonPosition = FabPosition.End,
      ) { paddingValues ->
        FlightsList(currentFlights, lightOrange, paddingValues, "Upcoming flights") { selectedFlight
          ->
          navController.navigate(Route.FLIGHT_DETAILS + "/${selectedFlight}")
        }
      }
    } else {
      Scaffold(
          modifier = Modifier.fillMaxSize(),
          bottomBar = { BottomBar(navController) },
          floatingActionButton = {
            // Define the FloatingActionButton to create a flight
          },
          floatingActionButtonPosition = FabPosition.End,
      ) { paddingValues ->
        FlightsList(currentFlights, Purple40, paddingValues, "Upcoming flights") { selectedFlight ->
          navController.navigate(Route.FLIGHT_DETAILS + "/${selectedFlight}")
        }
      }
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
