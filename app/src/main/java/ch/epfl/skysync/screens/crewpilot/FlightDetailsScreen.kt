package ch.epfl.skysync.screens.crewpilot

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.ConfirmedFlightDetailBottom
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.components.FinishedFlightDetailBottom
import ch.epfl.skysync.components.FlightDetails
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.lightGray
import ch.epfl.skysync.viewmodel.FinishedFlightsViewModel
import ch.epfl.skysync.viewmodel.FlightsViewModel
import ch.epfl.skysync.viewmodel.InFlightViewModel

@Composable
fun FlightDetailScreen(
    navController: NavHostController,
    flightId: String,
    viewModel: FlightsViewModel,
    inFlightViewModel: InFlightViewModel,
    finishedFlightsViewModel: FinishedFlightsViewModel
) {

  val uncompletedFlight by viewModel.getFlight(flightId).collectAsStateWithLifecycle()
  val finishedFlight by finishedFlightsViewModel.getFlight(flightId).collectAsStateWithLifecycle()
  val flight = if (uncompletedFlight != null) uncompletedFlight else finishedFlight
  Scaffold(
      topBar = { CustomTopAppBar(navController = navController, title = "Flight Detail") },
      bottomBar = {
        when (flight) {
          is ConfirmedFlight -> {
            ConfirmedFlightDetailBottom({ navController.popBackStack() }, {}, false)
          }
          is FinishedFlight -> {
            FinishedFlightDetailBottom(
                reportClick = { navController.navigate(Route.REPORT + "/${flightId}") },
                flightTraceClick = {
                  inFlightViewModel.startDisplayFlightTrace(flight)
                  navController.navigate(Route.FLIGHT)
                })
          }
        }
      },
      containerColor = lightGray) { padding ->
        if (flight == null) {
          LoadingComponent(isLoading = true, onRefresh = {}) {}
        } else {
          FlightDetails(flight = flight, padding = padding) {}
        }
      }
}
