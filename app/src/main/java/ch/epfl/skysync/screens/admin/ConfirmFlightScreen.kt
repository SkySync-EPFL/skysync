package ch.epfl.skysync.screens.admin

import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.Confirmation
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.components.SnackbarManager
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
    LoadingComponent(isLoading = true, onRefresh = {}) {}
  } else {
    if (flight !is PlannedFlight) {
      navController.navigate(Route.ADMIN_HOME)
      SnackbarManager.showMessage("This action is not possible on this type of flight")
      return
    }
    val plannedFlight = flight as PlannedFlight
    if (!plannedFlight.readyToBeConfirmed()) {
      navController.navigate(Route.ADMIN_HOME)
      SnackbarManager.showMessage("Flight cannot be confirmed")
      return
    }
    Confirmation(plannedFlight = plannedFlight) {
      viewModel.addConfirmedFlight(it)
      navController.navigate(Route.ADMIN_HOME)
    }
  }
}
