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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ch.epfl.skysync.database.DateUtility.localDateToWeekdayMonthDay
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.FlightStatus
import java.time.LocalDate

/**
 * represents a card for a flight*
 *
 * @param flight the flight to display
 * @param onFlightClick the action to perform when the card is clicked
 */
@Composable
fun FlightCard(flight: Flight, onFlightClick: (String) -> Unit) {
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
            FlightDateText(date = flight.date, modifier = Modifier.alignByBaseline())
            Spacer(modifier = Modifier.width(16.dp))
            FlightDetailsColumn(
                flight = flight, modifier = Modifier.weight(0.7f).padding(start = 16.dp))
            FlightStatusText(status = flight.getFlightStatus(), Modifier.alignByBaseline())
          }
    }
  }
}

/** displays the date of a flight */
@Composable
fun FlightDateText(date: LocalDate, modifier: Modifier) {
  Text(
      text = localDateToWeekdayMonthDay(date),
      style = MaterialTheme.typography.titleMedium,
      modifier = modifier, // modifier.alignByBaseline(),
      textAlign = TextAlign.Center,
      color = Color.Black)
}

/** displays the flight details consisting of the flight type, passenger count and time slot */
@Composable
fun FlightDetailsColumn(flight: Flight, modifier: Modifier) {
  Column(modifier = modifier) { // Modifier.weight(0.7f).padding(start = 16.dp)
    Text(
        text = "${flight.flightType.name} - ${flight.nPassengers} pax",
        fontWeight = FontWeight.Bold,
        color = Color.Black)
    Text(text = flight.timeSlot.toString(), color = Color.Gray)
  }
}

/** displays the flight status */
@Composable
fun FlightStatusText(status: FlightStatus, modifier: Modifier) {
  Text(
      text = status.text,
      style = MaterialTheme.typography.bodyMedium,
      color = Color.Gray,
      modifier = modifier)
}
