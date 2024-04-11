package ch.epfl.skysync.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.viewmodel.UserViewModel

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
      ShowFlightCalendar(navController, padding, viewModel)
    }
  }
}
