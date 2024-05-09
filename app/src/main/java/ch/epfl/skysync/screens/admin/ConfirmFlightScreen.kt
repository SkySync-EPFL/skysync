package ch.epfl.skysync.screens.admin

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import ch.epfl.skysync.components.confirmation
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.viewmodel.FlightsViewModel
import java.time.LocalDate

@Composable
fun confirmationScreenHardCoded(navController: NavController) {
  val dummy =
      PlannedFlight(
          "1234",
          3,
          FlightType.DISCOVERY,
          Team(listOf(Role(RoleType.CREW))),
          Balloon("Balloon Name", BalloonQualification.LARGE, "Ballon Name"),
          Basket("Basket Name", true, "1234"),
          LocalDate.now().plusDays(3),
          TimeSlot.PM,
          listOf(Vehicle("Peugeot 308", "1234")))
  confirmation(dummy) { navController.navigate(Route.MAIN) }
}

@Composable
fun confirmationScreen(navCtr: NavController, flightId: String, vm: FlightsViewModel) {
  confirmationScreenHardCoded(navController = navCtr)
}
