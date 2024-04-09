package ch.epfl.skysync.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import java.time.LocalDate

/**
 * Composable function to display flight information based on a given date and time slot.
 *
 * @param date The date for which the flight information is requested.
 * @param time The time slot AM or PM.
 * @param getFlightByDate A function that retrieves flight information based on date and time. It
 *   takes a LocalDate and a TimeSlot as input parameters and returns a Flight object representing
 *   the flight information for the specified date and time slot, or null if no flight exists for
 *   that date and time slot.
 * @param onClick Lambda function representing the action to perform when the button is clicked.
 */
@Composable
fun ShowFlight(
    date: LocalDate,
    time: TimeSlot,
    getFlightByDate: (LocalDate, TimeSlot) -> Flight?,
    onClick: () -> Unit
) {
  Button(onClick) {
    val flight = getFlightByDate(date, time)
    if (flight == null) {
      Text(text = "No flight")
    } else {
      Text(text = flight.flightType.name)
    }
  }
}
/**
 * Composable function to display a calendar with flight information for each date and time slot.
 *
 * @param onClick Lambda function representing the action to perform when a flight button is
 *   clicked.
 * @param getFlightByDate A function that retrieves flight information based on date and time. It
 *   takes a LocalDate and a TimeSlot as input parameters and returns a Flight object representing
 *   the flight information for the specified date and time slot, or null if no flight exists for
 *   that date and time slot.
 */
@Composable
fun ShowFlightCalendar(
    onClick: () -> Unit,
    getFlightByDate: (LocalDate, TimeSlot) -> Flight?,
    navController: NavHostController
) {
  Box() {
    Calendar { date, time, size -> ShowFlight(date, time, getFlightByDate, onClick) }
    Button(onClick = { onClick() }) { Text("Add Flight") }
  }
}

@Composable
@Preview
fun ShowFlightCalendarPreview() {
  val navController = rememberNavController()
  ShowFlightCalendar({}, FakeFlights, navController = navController)
}

val FakeFlights: (LocalDate, TimeSlot) -> Flight? = { date: LocalDate, timeslot: TimeSlot ->
  if ((LocalDate.now() == date) && (timeslot == TimeSlot.AM)) {
    PlannedFlight(
        "Test Flight",
        1,
        Team(mutableListOf(Role(RoleType.PILOT))),
        FlightType("Premium"),
        null,
        null,
        date,
        TimeSlot.AM,
        mutableListOf(Vehicle("car", "car1")))
  } else {
    null
  }
}
