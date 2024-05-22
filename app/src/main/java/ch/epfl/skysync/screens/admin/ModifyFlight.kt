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
fun ModifyFlightScreen(
    navController: NavHostController,
    viewModel: FlightsViewModel,
    flightId: String
) {
  val flightToModify by viewModel.getFlight(flightId).collectAsStateWithLifecycle()
  val allFlightTypes = viewModel.currentFlightTypes.collectAsStateWithLifecycle()
  val allBalloons = viewModel.currentBalloons.collectAsStateWithLifecycle()
  val allBaskets = viewModel.currentBaskets.collectAsStateWithLifecycle()
  val allVehicles = viewModel.currentVehicles.collectAsStateWithLifecycle()
  val availableUsers = viewModel.availableUsers.collectAsStateWithLifecycle()
  val allRoleTypes = RoleType.entries
  FlightForm(
      currentFlight = flightToModify,
      navController = navController,
      title = "Modify Flight",
      modifyMode = true,
      allFlightTypes = allFlightTypes,
      allRoleTypes = allRoleTypes,
      availableVehicles = allVehicles,
      availableBalloons = allBalloons,
      availableBaskets = allBaskets,
      availableUsers = availableUsers,
      onSaveFlight = { flight: PlannedFlight ->
        viewModel.modifyFlight(flight)
        navController.navigate(Route.ADMIN_HOME)
      },
      refreshDate = { date, timeSlot -> viewModel.setDateAndTimeSlot(date, timeSlot) },
  )
}
