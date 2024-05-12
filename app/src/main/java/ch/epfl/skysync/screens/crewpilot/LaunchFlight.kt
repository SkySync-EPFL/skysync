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
import ch.epfl.skysync.models.user.Pilot
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.viewmodel.FlightsViewModel
import ch.epfl.skysync.viewmodel.LocationViewModel

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
        }
        else if (personalFlights!!.isEmpty()) {
            if (user is Pilot) {
                LaunchFlightUi(
                    pilotBoolean = true,
                    flight = null,
                    paddingValues = padding,
                ){}
            } else {
                LaunchFlightUi(
                    pilotBoolean = false,
                    flight = null,
                    paddingValues = padding,
                ){}
            }
        }
        else if (currentFlightId == null) {
            if (user is Pilot) {
                LaunchFlightUi(
                    pilotBoolean = true,
                    flight = personalFlights!!.first(),
                    paddingValues = padding,
                )
                {
                    inFlightViewModel.setFlightId(it)
                }
            } else {
                LaunchFlightUi(
                    pilotBoolean = false,
                    flight = null,
                    paddingValues = padding,
                ){}
            }
        }
        else{
            navController.navigate(Route.FLIGHT)
        }

    }
}