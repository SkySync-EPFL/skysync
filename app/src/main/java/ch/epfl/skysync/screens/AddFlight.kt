package ch.epfl.skysync.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.ui.components.FlightForm

@Composable
fun AddFlightScreen(navController: NavHostController, flights: MutableList<PlannedFlight>) {
  FlightForm(
      navController = navController, flights = flights, currentFlight = null, title = "Add Flight")
}

@Composable
@Preview
fun AddFlightScreenPreview() {
  val navController = rememberNavController()
  AddFlightScreen(navController = navController, flights = mutableListOf())
}
