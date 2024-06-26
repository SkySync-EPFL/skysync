package ch.epfl.skysync.screens.crewpilot

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.ChatText
import ch.epfl.skysync.components.ConnectivityStatus
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.viewmodel.ChatViewModel

@Composable
fun TextScreen(
    navController: NavHostController,
    groupId: String,
    viewModel: ChatViewModel,
    connectivityStatus: ConnectivityStatus
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val messages by viewModel.getGroupChatMessages(groupId).collectAsStateWithLifecycle()
  val group = uiState.messageGroups.find { it.id == groupId }

  Scaffold(
      modifier = Modifier.testTag("ChatScreenScaffold"),
      topBar = {
        CustomTopAppBar(navController = navController, title = group?.name ?: "Group not found")
      },
      bottomBar = { BottomBar(navController) }) { padding ->
        ChatText(
            messages = messages,
            onSend = { content -> viewModel.sendMessage(groupId, content) },
            paddingValues = padding,
            connectivityStatus)
      }
}
