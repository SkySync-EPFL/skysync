package ch.epfl.skysync.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.ChatMessage
import ch.epfl.skysync.components.ChatText
import ch.epfl.skysync.components.MessageType
import ch.epfl.skysync.navigation.BottomBar

@Composable
fun TextScreen(navController: NavHostController, groupName: String) {
  val hardCoded =
      listOf(
          ChatMessage("Sender", MessageType.RECEIVED, null, "Message", "11:11"),
          ChatMessage("me", MessageType.SENT, null, "Message", "12:12"),
      )
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("ChatScreenScaffold"),
      bottomBar = { BottomBar(navController) }) { padding ->
        ChatText(
            groupName = groupName,
            messages = hardCoded,
            onBack = { navController.popBackStack() },
            onSend = { /*TODO*/},
            paddingValues = padding)
      }
}