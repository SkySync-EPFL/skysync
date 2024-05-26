package ch.epfl.skysync.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
fun Timer(modifier: Modifier, currentTimer: String, flightStage: FlightStage) {
  Box(modifier = modifier.testTag("Timer"), contentAlignment = Alignment.Center) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      if (flightStage != FlightStage.DISPLAY) {
        Text(
            modifier = Modifier.testTag("Timer Value"),
            text = currentTimer,
            style = MaterialTheme.typography.headlineMedium)
      }
    }
  }
}

@Composable
fun TimerButton(
    modifier: Modifier,
    flightStage: FlightStage,
    isPilot: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onClear: () -> Unit,
    onQuitDisplay: () -> Unit,
) {
  when (flightStage) {
    FlightStage.IDLE ->
        if (isPilot) {
          Button(
              modifier = modifier.testTag("Start Button"),
              onClick = { onStart() },
              colors = ButtonDefaults.buttonColors(containerColor = lightOrange)) {
                Text(text = "Start Flight")
              }
        }
    FlightStage.ONGOING ->
        if (isPilot) {
          Button(
              modifier = modifier.testTag("Stop Button"),
              onClick = { onStop() },
              colors = ButtonDefaults.buttonColors(containerColor = lightOrange)) {
                Text(text = "Stop Flight")
              }
        }
    FlightStage.POST ->
        Button(
            modifier = modifier.testTag("Clear Button"),
            onClick = { onClear() },
            colors = ButtonDefaults.buttonColors(containerColor = lightOrange)) {
              Text(text = "Exit Flight")
            }
    FlightStage.DISPLAY ->
        Button(
            modifier = modifier.testTag("Quit Button"),
            onClick = { onQuitDisplay() },
            colors = ButtonDefaults.buttonColors(containerColor = lightOrange)) {
              Text(text = "Quit display")
            }
  }
}

@Preview
@Composable
fun TimerPreview() {
  Timer(Modifier.padding(16.dp), "0:0:0", FlightStage.ONGOING)
}