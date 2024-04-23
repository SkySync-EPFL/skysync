package ch.epfl.skysync.components

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
fun GlobalSnackbarHost() {
  // Remember a SnackbarHostState which controls the snackbar queue.
  val snackbarHostState = remember { SnackbarHostState() }
  val snackbarManager = SnackbarManager

  // LaunchedEffect to listen to messages from SnackbarManager and show them as they arrive.
  LaunchedEffect(snackbarManager) {
    snackbarManager.messagesFlow.collect { message -> snackbarHostState.showSnackbar(message) }
  }

  // The actual SnackbarHost that will display the snackbars.
  SnackbarHost(
      hostState = snackbarHostState,
      snackbar = { snackbarData -> Snackbar(snackbarData = snackbarData) })
}

// Preview of how the snackbar system works within a Scaffold. Useful for seeing the behavior in the
// design tool.
// @Preview(showBackground = true)
// @Composable
// fun SnackbarPreview() {
 // MaterialTheme {
 //   Scaffold(snackbarHost = { GlobalSnackbarHost() }) { innerPadding ->
//      Box(
//          contentAlignment = Alignment.Center) {
  //          // An effect to send a test message when the Preview is launched.
//            LaunchedEffect(Unit) { SnackbarManager.showMessage("Preview: Error in database") }
//          }
 //   }
//  }
// }
