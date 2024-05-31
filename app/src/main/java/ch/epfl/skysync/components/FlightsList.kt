package ch.epfl.skysync.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ch.epfl.skysync.models.flight.Flight

/** shows a list of flights with a given title on the top and a banner color */
@Composable
fun FlightsList(
    flights: List<Flight>?,
    topBannerColor: Color,
    paddingValues: PaddingValues,
    topTitle: String,
    onFlightCardClick: (String) -> Unit
) {
  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    TopBanner(topTitle, topBannerColor, paddingValues)
    Spacer(modifier = Modifier.height(16.dp))
    FlightsListContent(
        flights = flights, paddingValues = paddingValues, onFlightClick = onFlightCardClick)
  }
}


/**
 * shows a list of flights. Distinguishes 3 cases:
 * 1) flights == null: shows a loading component
 * 2) flights is empty: shows a text "No flights"
 * 3) flights not empty: shows a the of flights
 */
@Composable
fun FlightsListContent(
    flights: List<Flight>?,
    paddingValues: PaddingValues,
    onFlightClick: (String) -> Unit
) {
  if (flights == null) {
    LoadingComponent(isLoading = true, onRefresh = {}) {}
  } else if (flights.isEmpty()) {
    NoAvailableFlights()
  } else {
    // Display the flights in a LazyColumn if the list is not empty
    val sortedFlights = flights.sortedBy { it.date }
    LazyColumn(modifier = Modifier.testTag("HomeLazyList").padding(paddingValues)) {
      items(sortedFlights) { flight -> FlightCard(flight, onFlightClick) }
    }
  }
}

/** shows a text "No flights" */
@Composable
fun NoAvailableFlights() {
  Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3f), contentAlignment = Alignment.Center) {
    Text(
        text = "No flights",
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
        color = Color.Black)
  }
}
