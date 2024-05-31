package ch.epfl.skysync.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.ui.theme.TOP_CORNER_ROUNDED

/**
 * A composable function that displays a list of flights with a given title at the top and a banner
 * color.
 *
 * @param flights The list of flights to be displayed. Can be null, in which case a loading
 *   component is shown.
 * @param topBannerColor The color of the top banner.
 * @param paddingValues The padding values to be applied to the list.
 * @param topTitle The title to be displayed at the top of the list.
 * @param onFlightCardClick The action to perform when a flight card is clicked. Receives the flight
 *   ID as a parameter.
 */
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
 * A composable function that displays the top banner with a title.
 *
 * @param topTitle The title to be displayed at the top of the banner.
 * @param topBannerColor The color of the top banner.
 * @param paddingValues The padding values to be applied to the banner.
 */
@Composable
fun TopBanner(topTitle: String, topBannerColor: Color, paddingValues: PaddingValues) {
  Text(
      text = topTitle,
      style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
      modifier =
          Modifier.background(color = topBannerColor, shape = TOP_CORNER_ROUNDED)
              .fillMaxWidth()
              .padding(
                  top = paddingValues.calculateTopPadding() + 16.dp,
                  start = 16.dp,
                  end = 16.dp,
                  bottom = 16.dp),
      color = Color.White,
      textAlign = TextAlign.Center)
}

/**
 * A composable function that displays the content of the flights list. It distinguishes 3 cases:
 * 1) flights == null: shows a loading component
 * 2) flights is empty: shows a text "No flights"
 * 3) flights not empty: shows a the of flights
 *
 * @param flights The list of flights to be displayed.
 * @param paddingValues The padding values to be applied to the list.
 * @param onFlightClick The action to perform when a flight card is clicked. Receives the flight ID
 *   as a parameter.
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

/** A composable function that displays a text "No flights" when there are no flights available. */
@Composable
fun NoAvailableFlights() {
  Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3f), contentAlignment = Alignment.Center) {
    Text(
        text = "No flights",
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
        color = Color.Black)
  }
}
