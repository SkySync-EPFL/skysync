package ch.epfl.skysync.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.components.FlightCard
import ch.epfl.skysync.components.FlightsList
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.models.flight.Flight
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
      FlightsList(userFlights.value, paddingValues) {
          selectedFlight ->
        navController.navigate(Route.ADMIN_FLIGHT_DETAILS + "/$selectedFlight")
      }
    }
  }
}
@Composable
fun FlightsList(
    flights: List<Flight>?,
    paddingValues: PaddingValues,
    onFlightClick: (String) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Completed Flights", modifier = Modifier
            .fillMaxWidth(), textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        if (flights == null) {
            LoadingComponent(isLoading = true, onRefresh = { /*TODO*/}) {}
        } else if (flights.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.3f),
                contentAlignment = Alignment.Center) {
                Text(
                    text = "No flights",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black)
            }
        } else {
            // Display the flights in a LazyColumn if the list is not empty
            LazyColumn(modifier = Modifier
                .testTag("HomeLazyList")
                .padding(horizontal = 16.dp)){
                items(flights) { flight -> FlightCard(flight, onFlightClick) }
            }
        }
    }
}
