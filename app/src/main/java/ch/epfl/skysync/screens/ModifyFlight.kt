package ch.epfl.skysync.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.forms.FlightForm
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.viewmodel.FlightsViewModel

@Composable
fun ModifyFlightScreen(
    navController: NavHostController,
    viewModel: FlightsViewModel,
    flightId: String
) {
  val allFlightTypes by viewModel.currentFlightTypes.collectAsStateWithLifecycle()
  val allBalloons by viewModel.currentBalloons.collectAsStateWithLifecycle()
  val allBaskets by viewModel.currentBaskets.collectAsStateWithLifecycle()
  val allVehicles by viewModel.currentVehicles.collectAsStateWithLifecycle()
  val flightToModify by viewModel.getFlight(flightId).collectAsStateWithLifecycle()
  val allRoleTypes = RoleType.entries
  FlightForm(
      currentFlight = flightToModify,
      title = "Modify Flight",
      modifyMode = true,
      allFlightTypes = allFlightTypes,
      allRoleTypes = allRoleTypes,
      allVehicles = allVehicles,
      allBalloons = allBalloons,
      allBaskets = allBaskets,
      flightAction = { flight: PlannedFlight ->
        viewModel.modifyFlight(flight)
        navController.navigate(Route.HOME)
      },
      onBackButton = { navController.popBackStack() })
}
