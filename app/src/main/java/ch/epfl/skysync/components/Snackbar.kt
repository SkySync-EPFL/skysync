package ch.epfl.skysync.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

// Singleton object to manage snackbar messages.
object SnackbarManager {
  // Using a Channel to handle message passing; conflated to drop old messages if new ones arrive
  // before they are displayed.
  private val messageChannel = Channel<String>(Channel.CONFLATED)

  // Function to enqueue a message to the snackbar. Messages are sent to the channel.
  fun showMessage(message: String) {
    messageChannel.trySend(message)
  }

  // Exposes the internal channel as a Flow to be collected by the composable layer.
  val messagesFlow = messageChannel.receiveAsFlow()
}

// Composable function that hosts a Snackbar. It listens for messages from the SnackbarManager.
@Composable
fun GlobalSnackbarHost(snackbarColor: Color = Color.Black) {
  // Remember a SnackbarHostState which controls the snackbar queue.
  val snackbarHostState = remember { SnackbarHostState() }
  val snackbarManager = SnackbarManager

  // LaunchedEffect to listen to messages from SnackbarManager and show them as they arrive.
  LaunchedEffect(snackbarManager) {
    snackbarManager.messagesFlow.collect { message -> snackbarHostState.showSnackbar(message) }
  }

  // The actual SnackbarHost that will display the snackbars. Colors and other UI properties can be
  // customized here.
  SnackbarHost(
      hostState = snackbarHostState,
      snackbar = { snackbarData ->
        // Customize the appearance of Snackbar. Here, the container color can be dynamically
        // changed.
        Snackbar(snackbarData = snackbarData, containerColor = snackbarColor)
      })
}

// Preview of how the snackbar system works within a Scaffold. Useful for seeing the behavior in the
// design tool.
@Preview(showBackground = true)
@Composable
fun SnackbarPreview() {
  MaterialTheme {
    Scaffold(snackbarHost = { GlobalSnackbarHost(snackbarColor = Color.Red) }) { innerPadding ->
      Box(
          modifier = Modifier.fillMaxSize().padding(innerPadding),
          contentAlignment = Alignment.Center) {
            // An effect to send a test message when the Preview is launched.
            LaunchedEffect(Unit) { SnackbarManager.showMessage("Preview: Error in database") }
          }
    }
  }
}
