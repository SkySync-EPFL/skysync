package ch.epfl.skysync.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ch.epfl.skysync.models.flight.Flight
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun FlightCard(flight: Flight, onFlightClick: (String) -> Unit) {
  // Card for an individual flight, clickable to navigate to details
  Card(
      modifier =
          Modifier.fillMaxWidth()
              .clickable { onFlightClick(flight.id) }
              .padding(vertical = 4.dp)
              .testTag("flightCard${flight.id}"),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
  ) {
    Surface(modifier = Modifier.fillMaxWidth(), color = flight.getFlightStatus().displayColor) {
      Row(
          modifier = Modifier.fillMaxWidth().padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Start) {
            Text(
                text =
                    flight.date.format(
                        DateTimeFormatter.ofPattern("E\nMMM dd").withLocale(Locale.ENGLISH)),
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
            Text(
                text = flight.getFlightStatus().toString(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.alignByBaseline(),
                color = Color.Gray)
          }
    }
  }
}
