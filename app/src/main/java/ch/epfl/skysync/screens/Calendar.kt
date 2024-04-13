package ch.epfl.skysync.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.AvailabilityCalendar
import ch.epfl.skysync.components.FlightCalendar
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.viewmodel.CalendarViewModel

@Composable
fun CalendarScreen(
    navController: NavHostController,
    calendarType: String,
    viewModel: CalendarViewModel
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomBar(navController) }) { padding ->
        if (calendarType == Route.AVAILABILITY_CALENDAR) {
            AvailabilityCalendar(viewModel, padding, navController)
        } else if (calendarType == Route.PERSONAL_FLIGHT_CALENDAR) {
            FlightCalendar(viewModel, padding, navController, onFlightClick = {})
        }
    }
}
