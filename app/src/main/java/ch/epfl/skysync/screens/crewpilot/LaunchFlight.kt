package ch.epfl.skysync.screens.crewpilot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.LaunchFlightUi
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.user.Pilot
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.viewmodel.FlightsViewModel
import ch.epfl.skysync.viewmodel.LocationViewModel
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun LaunchFlight(
    navController: NavHostController,
    flightsViewModel: FlightsViewModel,
    inFlightViewModel: LocationViewModel,
) {
  Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = { BottomBar(navController) }) { padding ->
    // Renders the Google Map or a permission request message based on the permission status.
    val user by flightsViewModel.currentUser.collectAsStateWithLifecycle()
    val personalFlights by inFlightViewModel.personalFlights.collectAsStateWithLifecycle()
    val currentFlightId by inFlightViewModel.flightId.collectAsStateWithLifecycle()
    if (personalFlights == null) {
      LoadingComponent(isLoading = true, onRefresh = {}) {}
    } else if (personalFlights!!.isEmpty()) {
      LaunchFlightUi(
          pilotBoolean = user is Pilot,
          flight = null,
          paddingValues = padding,
      ) {}
    } else if (currentFlightId == null) {
      val time = if (LocalTime.now().isAfter(LocalTime.of(12, 0))) TimeSlot.PM else TimeSlot.AM
      if (user is Pilot &&
          personalFlights!!.first().date == LocalDate.now() &&
          personalFlights!!.first().timeSlot == time) {
        LaunchFlightUi(
            pilotBoolean = true,
            flight = personalFlights!!.first(),
            paddingValues = padding,
        ) {
          inFlightViewModel.setFlightId(it)
        }
      } else {
        LaunchFlightUi(
            pilotBoolean = user is Pilot,
            flight = null,
            paddingValues = padding,
        ) {}
      }
    } else {
      navController.navigate(Route.FLIGHT)
    }
  }
}
