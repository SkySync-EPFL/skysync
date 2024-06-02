package ch.epfl.skysync.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
 * A composable function that displays a timer and a button to start or stop the timer based on the
 * flight stage.
 *
 * @param modifier The modifier to be applied to the timer.
 * @param currentTimer The current value of the timer.
 * @param flightStage The current stage of the flight.
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

/**
 * A composable function that displays a button to start, stop, clear or quit the flight based on
 * the flight stage.
 *
 * @param modifier The modifier to be applied to the button.
 * @param flightStage The current stage of the flight.
 * @param isPilot A boolean indicating if the user is a pilot.
 * @param onStart The action to perform when the start button is clicked.
 * @param onStop The action to perform when the stop button is clicked.
 * @param onClear The action to perform when the clear button is clicked.
 * @param onQuitDisplay The action to perform when the quit button is clicked.
 */
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
