package ch.epfl.skysync.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.viewmodel.FlightsViewModel

@Composable
fun FlightHistoryScreen(navController: NavHostController, viewModel: FlightsViewModel) {
  val allFlights by viewModel.currentFlights.collectAsStateWithLifecycle()
  if (allFlights == null || allFlights!!.isEmpty()) {
    Text("No flights available")
  } else {
    LazyColumn {
        items(allFlights!!) { flight ->
            HistoryRow(flight)
        }
    }
  }
}

@Composable
fun HistoryRow(flight: Flight) {
  Row{
      Text(flight.date.toString())

  }
}