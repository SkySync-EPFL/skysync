package ch.epfl.skysync.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.FlightType.Companion.DISCOVERY
import ch.epfl.skysync.models.flight.FlightType.Companion.FONDUE
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.ui.theme.lightGray
import ch.epfl.skysync.ui.theme.lightOrange
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// Sample list for preview (to be deleted)
val listFlights =
    listOf(
        PlannedFlight(
            nPassengers = 4,
            date = LocalDate.of(2024, 3, 19),
            timeSlot = TimeSlot.AM,
            flightType = FONDUE,
            vehicles = listOf(),
            balloon = null,
            basket = null,
            id = UNSET_ID),
        PlannedFlight(
            nPassengers = 2,
            date = LocalDate.of(2024, 3, 20),
            timeSlot = TimeSlot.AM,
            flightType = DISCOVERY,
            vehicles = listOf(),
            balloon = null,
            basket = null,
            id = UNSET_ID),
        PlannedFlight(
            nPassengers = 3,
            date = LocalDate.of(2024, 3, 22),
            timeSlot = TimeSlot.PM,
            flightType = DISCOVERY,
            vehicles = listOf(),
            balloon = null,
            basket = null,
            id = UNSET_ID),
    )
// Sample empty list for preview (to be deleted)
val emptyList: List<PlannedFlight> = emptyList()

@Composable
fun UpcomingFlights(flights: List<Flight>, onFlightClick: (Flight) -> Unit) {
  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Text(
        text = "Upcoming flights",
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        modifier =
            Modifier.background(
                    color = lightOrange,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .fillMaxWidth()
                .padding(16.dp),
        color = Color.White,
        textAlign = TextAlign.Center)

    Spacer(modifier = Modifier.height(16.dp))

    if (flights.isEmpty()) {
      // Handle case when no upcoming flights
      Box(
          modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3f),
          contentAlignment = Alignment.Center) {
            Text(
                text = "No upcoming flights",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.Gray)
          }
    } else {
      // Display the flights in a LazyColumn if the list is not empty
      LazyColumn { items(flights) { flight -> FlightRow(flight, onFlightClick) } }
    }
  }
}

@Composable
fun FlightRow(flight: Flight, onFlightClick: (Flight) -> Unit) {
  // Card for an individual flight, clickable to navigate to details
  Card(
      modifier =
          Modifier.fillMaxWidth().clickable { onFlightClick(flight) }.padding(vertical = 4.dp),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
  ) {
    Surface(modifier = Modifier.fillMaxWidth(), color = lightGray) {
      Row(
          modifier = Modifier.fillMaxWidth().padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Start) {
            Text(
                text =
                    flight.date.format(
                        DateTimeFormatter.ofPattern("E\ndd").withLocale(Locale.ENGLISH)),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.alignByBaseline(),
                color = Color.Black)
            // Spacer for horizontal separation
            Spacer(modifier = Modifier.width(16.dp))
            // Column for flight details
            Column(modifier = Modifier.weight(0.7f).padding(start = 16.dp)) {
              // Text for flight type and passenger count
              Text(
                  text = "${flight.flightType.name} - ${flight.nPassengers} pax",
                  fontWeight = FontWeight.Bold,
                  color = Color.Black)
              // Text for flight time slot
              Text(text = flight.timeSlot.toString(), color = Color.Gray)
            }
          }
    }
  }
}

// Scaffold wrapper for the Home Screen
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavHostController) {
  Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = { BottomBar(navController) }) { padding ->
    // Calls UpcomingFlights composable with navigation logic
    UpcomingFlights(listFlights) { selectedFlight ->
      navController.navigate(
          "FlightDetailsScreen/${selectedFlight.id}") // route to the details of the selected flight
      // (to be modified)
    }
  }
}

// Preview provider for the Home Screen
@Composable
@Preview
fun HomeScreenPreview() {
  // Preview navigation controller
  val navController = rememberNavController()
  // Preview of Home Screen
  HomeScreen(navController = navController)
}
