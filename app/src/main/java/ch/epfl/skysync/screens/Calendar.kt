package ch.epfl.skysync.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.AvailabilityCalendar
import ch.epfl.skysync.components.FlightCalendar
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.viewmodel.CalendarViewModel

@Composable
fun CalendarScreen(
    navController: NavHostController,
    calendarType: String,
    viewModel: CalendarViewModel
) {
  Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = { BottomBar(navController) }) { padding ->
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (calendarType == Route.AVAILABILITY_CALENDAR) {
      val availabilityCalendar = uiState.availabilityCalendar
      AvailabilityCalendar(
          padding,
          onFlightCalendarClick = { navController.navigate(Route.PERSONAL_FLIGHT_CALENDAR) },
          getAvailabilityStatus = { date, time ->
            availabilityCalendar?.getAvailabilityStatus(date, time) ?: AvailabilityStatus.UNDEFINED
          },
          nextAvailabilityStatus = { date, time ->
            availabilityCalendar?.nextAvailabilityStatus(date, time) ?: AvailabilityStatus.UNDEFINED
          },
          onSave = { viewModel.saveAvailabilities() })
    } else if (calendarType == Route.PERSONAL_FLIGHT_CALENDAR) {
      val flightCalendar = uiState.flightGroupCalendar
      FlightCalendar(
          padding,
          onAvailabilityCalendarClick = { navController.navigate(Route.AVAILABILITY_CALENDAR) },
          getFirstFlightByDate = { date, time -> flightCalendar?.getFirstFlightByDate(date, time) },
          onFlightClick = {
            // TODO: navigate to flight details screen
          })
    }
  }
}
