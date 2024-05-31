package ch.epfl.skysync.screens.admin

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
import ch.epfl.skysync.components.ConnectivityStatus
import ch.epfl.skysync.components.FlightsList
import ch.epfl.skysync.navigation.AdminBottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.lightOrange
import ch.epfl.skysync.viewmodel.FlightsViewModel

/**
 * The screen for the admin home
 *
 * @param navController The navigation controller
 * @param viewModel The view model
 * @param connectivityStatus The connectivity status
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AdminHomeScreen(
    navController: NavHostController,
    viewModel: FlightsViewModel,
    connectivityStatus: ConnectivityStatus
) {
  val currentFlights by viewModel.currentFlights.collectAsStateWithLifecycle()
  // Display the Home Screen with the list of upcoming flights
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      bottomBar = { AdminBottomBar(navController = navController) },
      floatingActionButton = {
        // Define the FloatingActionButton to create a flight
        if (connectivityStatus.isOnline()) {
          FloatingActionButton(
              modifier = Modifier.testTag("addFlightButton"),
              onClick = { navController.navigate(Route.ADD_FLIGHT) { launchSingleTop = true } },
              containerColor = lightOrange) {
                Icon(
                    imageVector = Icons.Default.Add, contentDescription = "Add", tint = Color.White)
              }
        }
      },
      floatingActionButtonPosition = FabPosition.End,
  ) { padding ->
    FlightsList(currentFlights, lightOrange, padding, "Upcoming flights") { selectedFlight ->
      navController.navigate(Route.ADMIN_FLIGHT_DETAILS + "/${selectedFlight}")
    }
  }
}
