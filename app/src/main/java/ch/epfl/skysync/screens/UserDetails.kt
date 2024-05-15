package ch.epfl.skysync.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.components.FlightsList
import ch.epfl.skysync.navigation.AdminBottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.lightOrange
import ch.epfl.skysync.viewmodel.FlightsViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserDetailsScreen(navController: NavHostController, flightsViewModel: FlightsViewModel) {
  val userFlights = flightsViewModel.currentFlights.collectAsStateWithLifecycle()
  val user = flightsViewModel.currentUser.collectAsStateWithLifecycle()

  // loads when it waits for the user to be fetched
  if (user.value == null) {
    LoadingScreen(navController = navController, viewModel = flightsViewModel)
  } else {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
          CustomTopAppBar(
              navController = navController,
              title = "${user.value!!.firstname} ${user.value!!.lastname}")
        },
        bottomBar = { AdminBottomBar(navController = navController) },
        floatingActionButtonPosition = FabPosition.End,
    ) { paddingValues ->
      FlightsList(userFlights.value, lightOrange, paddingValues, "Completed Flights") {
          selectedFlight ->
        navController.navigate(Route.ADMIN_FLIGHT_DETAILS + "/$selectedFlight")
      }
    }
  }
}
