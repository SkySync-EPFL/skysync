package ch.epfl.skysync.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.ui.components.FlightForm
import java.time.LocalDate

@Composable
fun ModifyFlightScreen(
    navController: NavHostController,
    flights: MutableList<PlannedFlight>,
    currentFlight: PlannedFlight
) {
  FlightForm(
      navController = navController,
      flights = flights,
      currentFlight = currentFlight,
      title = "Modify Flight")
}

@Composable
@Preview
fun ModifyFlightScreenPreview() {
  val navController = rememberNavController()
  val currentFlight =
      PlannedFlight(
          nPassengers = 1,
          date = LocalDate.now(),
          flightType = FlightType.PREMIUM,
          timeSlot = TimeSlot.AM,
          vehicles = listOf(),
          balloon = null,
          basket = null,
          id = "testId")
  ModifyFlightScreen(navController = navController, flights = mutableListOf(), currentFlight)
}
