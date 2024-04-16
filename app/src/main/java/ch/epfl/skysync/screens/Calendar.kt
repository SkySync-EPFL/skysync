package ch.epfl.skysync.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.viewmodel.UserViewModel
import java.time.LocalDate

@Composable
fun CalendarScreen(
    navController: NavHostController,
    calendarType: String,
    viewModel: UserViewModel
) {
  Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = { BottomBar(navController) }) { padding ->
    if (calendarType == Route.AVAILABILITY_CALENDAR) {
      showCalendarAvailabilities(navController, padding, viewModel)
    } else if (calendarType == Route.PERSONAL_FLIGHT_CALENDAR) {
      val dummyFlight =
          PlannedFlight(
              UNSET_ID,
              1,
              FlightType.FONDUE,
              balloon =null,
              basket = null,
              date = LocalDate.now(),
              timeSlot = TimeSlot.AM,
              vehicles =  listOf())
      val dummyFlight2 =
          PlannedFlight(
              UNSET_ID,
              1,
              FlightType.DISCOVERY,
              balloon = null,
              basket = null,
              date = LocalDate.now().plusDays(1),
              timeSlot = TimeSlot.PM,
              vehicles = listOf())
      viewModel.user.value.assignedFlights.addFlightByDate(
          date = dummyFlight.date, timeSlot = dummyFlight.timeSlot, dummyFlight)
      viewModel.user.value.assignedFlights.addFlightByDate(
          date = dummyFlight2.date, timeSlot = dummyFlight2.timeSlot, dummyFlight2)
      ShowFlightCalendar(navController, padding, viewModel)
    }
  }
}
