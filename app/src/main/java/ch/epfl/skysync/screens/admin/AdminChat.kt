package ch.epfl.skysync.screens.admin

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.GroupChat
import ch.epfl.skysync.navigation.AdminBottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.viewmodel.ChatViewModel

@Composable
fun AdminChatScreen(navController: NavHostController, viewModel: ChatViewModel) {
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("AdminChatScreenScaffold"),
      bottomBar = { AdminBottomBar(navController) },
  ) { padding ->
    val groupDetails by viewModel.getGroupDetails().collectAsStateWithLifecycle()
    GroupChat(
        groupList = groupDetails,
        onClick = { selectedGroup ->
          navController.navigate(Route.ADMIN_TEXT + "/${selectedGroup.id}")
        },
        paddingValues = padding,
        true)
  }
}

/*
@Composable
@Preview
fun Preview() {
  val navController = rememberNavController()
  ChatScreen(navController = navController)
}
*/
