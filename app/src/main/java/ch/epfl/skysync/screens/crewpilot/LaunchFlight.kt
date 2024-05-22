package ch.epfl.skysync.screens.crewpilot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.ConnectivityStatus
import ch.epfl.skysync.components.ConnectivityWrapper
import ch.epfl.skysync.components.LaunchFlightUi
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.models.user.Pilot
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.viewmodel.FlightsViewModel
import ch.epfl.skysync.viewmodel.InFlightViewModel

@Composable
fun LaunchFlight(
    navController: NavHostController,
    flightViewModel: FlightsViewModel,
    inFlightViewModel: InFlightViewModel,
    connectivityStatus: ConnectivityStatus
) {

  Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = { BottomBar(navController) }) { padding ->
    // Renders the Google Map or a permission request message based on the permission status.
    val user by flightViewModel.currentUser.collectAsStateWithLifecycle()
    val loading by inFlightViewModel.loading.collectAsStateWithLifecycle()
    val currentFlight by inFlightViewModel.currentFlight.collectAsStateWithLifecycle()
    val startableFlight by inFlightViewModel.startableFlight.collectAsStateWithLifecycle()
    ConnectivityWrapper(connectivityStatus = connectivityStatus, navController = navController) {
      if (loading) {
        LoadingComponent(isLoading = true, onRefresh = {}) {}
      } else if (currentFlight == null) {
        LaunchFlightUi(
            pilotBoolean = user is Pilot,
            flight = if (user is Pilot) startableFlight else null,
            paddingValues = padding,
        ) {
          inFlightViewModel.setCurrentFlight(it)
        }
      } else {
        navController.navigate(Route.FLIGHT)
      }
    }
  }
}
