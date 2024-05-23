package ch.epfl.skysync.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.forms.FlightForm
import ch.epfl.skysync.components.forms.FlightForm2
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.viewmodel.FlightsViewModel

@Composable
fun AddFlightScreen(navController: NavHostController, viewModel: FlightsViewModel) {

  val allFlightTypes = viewModel.currentFlightTypes.collectAsStateWithLifecycle()
  val allBalloons = viewModel.currentBalloons.collectAsStateWithLifecycle()
  val allBaskets = viewModel.currentBaskets.collectAsStateWithLifecycle()
  val allVehicles = viewModel.currentVehicles.collectAsStateWithLifecycle()
  val allRoleTypes = RoleType.entries
  val availableUsers = viewModel.availableUsers.collectAsStateWithLifecycle()
  FlightForm(
      currentFlight = null,
      navController = navController,
      modifyFlight = false,
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
      },
      refreshDate = { date, timeSlot -> viewModel.setDateAndTimeSlot(date, timeSlot) },
  )
}
