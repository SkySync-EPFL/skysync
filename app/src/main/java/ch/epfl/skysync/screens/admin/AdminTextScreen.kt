package ch.epfl.skysync.screens.admin

import androidx.compose.foundation.layout.fillMaxSize
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
import ch.epfl.skysync.navigation.AdminBottomBar
import ch.epfl.skysync.viewmodel.ChatViewModel

/**
 * The screen for the admin chat
 *
 * @param navController The navigation controller
 * @param groupId The group id
 * @param viewModel The view model
 * @param connectivityStatus The connectivity status
 */
@Composable
fun AdminTextScreen(
    navController: NavHostController,
    groupId: String,
    viewModel: ChatViewModel,
    connectivityStatus: ConnectivityStatus
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val messages by viewModel.getGroupChatMessages(groupId).collectAsStateWithLifecycle()
  val group = uiState.messageGroups.find { it.id == groupId }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("ChatScreenScaffold"),
      bottomBar = { AdminBottomBar(navController) },
      topBar = {
        CustomTopAppBar(navController = navController, title = group?.name ?: "Group not found")
      }) { padding ->
        ChatText(
            messages = messages,
            onSend = { content -> viewModel.sendMessage(groupId, content) },
            paddingValues = padding,
            connectivityStatus)
      }
}
