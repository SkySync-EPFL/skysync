package ch.epfl.skysync.screens.crewpilot

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.ConfirmedFlightDetailBottom
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.components.FlightDetails
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.ui.theme.lightGray
import ch.epfl.skysync.viewmodel.FlightsViewModel

@Composable
fun FlightDetailScreen(
    navController: NavHostController,
    flightId: String,
    viewModel: FlightsViewModel
) {

  val flight by viewModel.getFlight(flightId).collectAsStateWithLifecycle()
  val user by viewModel.currentUser.collectAsStateWithLifecycle()
  Scaffold(
      topBar = { CustomTopAppBar(navController = navController, title = "Flight Detail") },
      containerColor = lightGray) { padding ->
        if (flight == null || user == null) {
          LoadingComponent(isLoading = true, onRefresh = {}) {}
        } else {
          FlightDetails(flight = flight, padding = padding) {
            ConfirmedFlightDetailBottom({ navController.popBackStack() }, {}, false)
          }
        }
      }
}
