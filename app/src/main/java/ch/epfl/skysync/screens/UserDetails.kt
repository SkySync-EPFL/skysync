package ch.epfl.skysync.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.components.FlightsListContent
import ch.epfl.skysync.navigation.AdminBottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.viewmodel.FlightsViewModel

/**
 * The user details screen
 *
 * @param navController The navigation controller
 * @param flightsViewModel The view model
 */
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
      Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Completed Flights",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge)
        FlightsListContent(
            flights = userFlights.value,
            paddingValues = PaddingValues(horizontal = 16.dp, vertical = 16.dp)) { selectedFlight ->
              navController.navigate(Route.ADMIN_FLIGHT_DETAILS + "/$selectedFlight")
            }
      }
    }
  }
}
