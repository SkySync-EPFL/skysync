package ch.epfl.skysync.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import ch.epfl.skysync.ui.theme.Purple40

@Composable
fun LaunchFlightUi(
    pilotBoolean: Boolean,
    flight: Flight?,
    paddingValues: PaddingValues,
    flightClick: (String) -> Unit
) {
  Column(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Launch Flight",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier =
                Modifier.background(
                        color = Purple40,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .fillMaxWidth()
                    .padding(
                        top = paddingValues.calculateTopPadding() + 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp),
            color = Color.White,
            textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        if (pilotBoolean) {
          if (flight == null) {
            Text(
                text = "No flight ready to be launched",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp),
                color = Color.Black,
            )
          } else {
            FlightCard(flight = flight) { flightClick(it) }
          }
        } else {
          Text(
              text = "No flight started",
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.padding(16.dp),
              color = Color.Black)
        }
      }
}
