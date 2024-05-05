package ch.epfl.skysync.screens.admin

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
fun AddFlightScreen(navController: NavHostController, viewModel: FlightsViewModel) {
  val allFlightTypes by viewModel.currentFlightTypes.collectAsStateWithLifecycle()
  val allBalloons by viewModel.currentBalloons.collectAsStateWithLifecycle()
  val allBaskets by viewModel.currentBaskets.collectAsStateWithLifecycle()
  val allVehicles by viewModel.currentVehicles.collectAsStateWithLifecycle()
  val allRoleTypes = RoleType.entries
  val availableUsers by viewModel.availableUsers.collectAsStateWithLifecycle()
  FlightForm(
      currentFlight = null,
      navController = navController,
      modifyMode = false,
      title = "Add Flight",
      allFlightTypes = allFlightTypes,
      allRoleTypes = allRoleTypes,
      availableVehicles = allVehicles,
      availableBalloons = allBalloons,
      availableBaskets = allBaskets,
      availableUsers = availableUsers,
      onSaveFlight = { flight: PlannedFlight ->
        viewModel.addFlight(flight)
        navController.navigate(Route.ADMIN_HOME)
      })
}
