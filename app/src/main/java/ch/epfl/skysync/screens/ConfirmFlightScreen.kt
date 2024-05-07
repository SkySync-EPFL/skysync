package ch.epfl.skysync.screens

import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.Confirmation
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.*
import ch.epfl.skysync.viewmodel.FlightsViewModel

@Composable
fun ConfirmationScreen(
    navController: NavHostController,
    flightId: String,
    viewModel: FlightsViewModel
) {
  val flight by viewModel.getFlight(flightId).collectAsStateWithLifecycle()

  if (flight == null) {
    LoadingComponent(isLoading = true, onRefresh = { /*TODO*/}) {}
  } else {
    if (flight !is PlannedFlight) {
      /* TODO error*/
    } else {
      Confirmation(plannedFlight = flight as PlannedFlight) {
        viewModel.addConfirmedFlight(it)
        navController.navigate(Route.HOME)
      }
    }
  }
}
