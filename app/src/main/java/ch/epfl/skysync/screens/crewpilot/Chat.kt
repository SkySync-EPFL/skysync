package ch.epfl.skysync.screens.crewpilot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.GroupChat
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.viewmodel.ChatViewModel

/**
 * The screen for the chat for the crew/pilot user
 *
 * @param navController The navigation controller
 * @param viewModel The view model
 */
@Composable
fun ChatScreen(navController: NavHostController, viewModel: ChatViewModel) {
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("ChatScreenScaffold"),
      bottomBar = { BottomBar(navController) },
  ) { padding ->
    val groupDetails by viewModel.getGroupDetails().collectAsStateWithLifecycle()
    val sortedGroups = groupDetails.sortedByDescending { it.lastMessage?.date }
    GroupChat(
        groupList = sortedGroups,
        onClick = { selectedGroup ->
          navController.navigate(Route.CREW_TEXT + "/${selectedGroup.id}")
        },
        onDelete = {},
        paddingValues = padding,
        isAdmin = false)
  }
}
