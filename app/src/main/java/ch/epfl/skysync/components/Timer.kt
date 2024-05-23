package ch.epfl.skysync.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.epfl.skysync.ui.theme.lightOrange
import ch.epfl.skysync.viewmodel.InFlightViewModel.FlightStage

/**
 * Timer composable that displays the current timer value and a button to start (if the timer is
 * currently not running) or stop (if the timer is currently running).
 *
 * @param modifier Modifier to apply to this layout node.
 * @param currentTimer The current value of the timer.
 * @param flightStage The current flight stage.
 * @param isPilot If the user is the pilot of the flight.
 * @param onStart Callback to start the flight.
 * @param onStop Callback to stop the flight.
 * @param onClear Callback to clear the flight.
 * @param onQuitDisplay Callback to quit the flight trace display.
 */
@Composable
fun Timer(
    modifier: Modifier,
    currentTimer: String,
    flightStage: FlightStage,
    isPilot: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onClear: () -> Unit,
    onQuitDisplay: () -> Unit,
) {
  Box(
      modifier = modifier.testTag("Timer"),
      contentAlignment = androidx.compose.ui.Alignment.Center) {
        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
          if (flightStage != FlightStage.DISPLAY) {
            Text(
                modifier = Modifier.testTag("Timer Value"),
                text = currentTimer,
                style = MaterialTheme.typography.headlineMedium)
          }

          when (flightStage) {
            FlightStage.IDLE ->
                if (isPilot) {
                  Button(
                      modifier = Modifier.testTag("Start Button"),
                      onClick = { onStart() },
                      colors = ButtonDefaults.buttonColors(containerColor = lightOrange)) {
                        Text(text = "Start Flight")
                      }
                }
            FlightStage.ONGOING ->
                if (isPilot) {
                  Button(modifier = Modifier.testTag("Stop Button"), onClick = { onStop() }) {
                    Text(text = "Stop Flight")
                  }
                }
            FlightStage.POST ->
                Button(modifier = Modifier.testTag("Clear Button"), onClick = { onClear() }) {
                  Text(text = "Exit Flight")
                }
            FlightStage.DISPLAY ->
                Button(modifier = Modifier.testTag("Quit Button"), onClick = { onQuitDisplay() }) {
                  Text(text = "Quit display")
                }
          }
        }
      }
}

@Preview
@Composable
fun TimerPreview() {
  Timer(Modifier.padding(16.dp), "0:0:0", FlightStage.ONGOING, true, {}, {}, {}, {})
}
