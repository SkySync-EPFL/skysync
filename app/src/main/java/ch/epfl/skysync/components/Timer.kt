package ch.epfl.skysync.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.epfl.skysync.ui.theme.lightOrange
import kotlinx.coroutines.delay

@Composable
fun Timer(modifier: Modifier,
          currentTimer: String,
          isRunning: Boolean,
          onStart: () -> Unit,
          onStop: () -> Unit)
{
  Box(
      modifier = modifier.testTag("Timer"),
      contentAlignment = androidx.compose.ui.Alignment.Center) {
        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
          Text(
              modifier = Modifier.testTag("Timer Value"),
              text = currentTimer,
              style = MaterialTheme.typography.headlineMedium)
          if (isRunning) {
            Button(
                modifier = Modifier.testTag("Reset Button"),
                onClick = {
                    onStop()
                }) {
                  Text(text = "Stop Flight")
                }
          } else {
            Button(
                modifier = Modifier.testTag("Start Button"),
                onClick = {
                    onStart()},
                colors = ButtonDefaults.buttonColors(containerColor = lightOrange)) {
                  Text(text = "Start Flight")
                }
          }
        }
      }
}

// Format the time in HH:MM:SS format from seconds
fun Int.formatTime(): String {
  val hours = this / 3600
  val minutes = (this % 3600) / 60
  val remainingSeconds = this % 60
  return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
}

@Preview
@Composable
fun TimerPreview() {
  Timer(Modifier.padding(16.dp), "0:0:0", false, {}, {})
}
