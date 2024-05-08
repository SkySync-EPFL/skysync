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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ch.epfl.skysync.models.flight.Flight

@Composable
fun FlightsList(
    flights: List<Flight>?,
    color: Color,
    paddingValues: PaddingValues,
    title: String,
    onFlightClick: (String) -> Unit
) {
  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        modifier =
            Modifier.background(
                    color = color, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .fillMaxWidth()
                .padding(
                    top = paddingValues.calculateTopPadding() + 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp),
        color = Color.White,
        textAlign = TextAlign.Center)

    Spacer(modifier = Modifier.height(16.dp))
    if (flights == null) {
      LoadingComponent(isLoading = true, onRefresh = { /*TODO*/}) {}
    } else if (flights.isEmpty()) {
      Box(
          modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3f),
          contentAlignment = Alignment.Center) {
            Text(
                text = "No flights",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.Black)
          }
    } else {
      // Display the flights in a LazyColumn if the list is not empty
      LazyColumn { items(flights) { flight -> FlightCard(flight, onFlightClick) } }
    }
  }
}
