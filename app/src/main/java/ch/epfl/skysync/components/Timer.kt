package ch.epfl.skysync.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.epfl.skysync.ui.theme.lightOrange

/**
 * Timer composable that displays the current timer value and a button to start (if the timer is
 * currently not running) or stop (if the timer is currently running).
 *
 * @param modifier Modifier to apply to this layout node.
 * @param currentTimer The current value of the timer.
 * @param isRunning The current state of the timer.
 * @param onStart Callback to start the timer.
 * @param onStop Callback to stop the timer.
 */
@Composable
fun Timer(
    modifier: Modifier,
    currentTimer: String,
    isRunning: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
  Box(
      modifier = modifier.testTag("Timer"),
      contentAlignment = androidx.compose.ui.Alignment.Center) {
        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
          Text(
              modifier = Modifier.testTag("Timer Value"),
              text = currentTimer,
              style = MaterialTheme.typography.headlineMedium)
          if (isRunning) {
            Button(modifier = Modifier.testTag("Stop Button"), onClick = { onStop() }) {
              Text(text = "Stop Flight")
            }
          } else {
            Button(
                modifier = Modifier.testTag("Start Button"),
                onClick = { onStart() },
                colors = ButtonDefaults.buttonColors(containerColor = lightOrange)) {
                  Text(text = "Start Flight")
                }
          }
        }
      }
}

@Preview
@Composable
fun TimerPreview() {
  Timer(Modifier.padding(16.dp), "0:0:0", false, {}, {})
}
