package ch.epfl.skysync.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.components.GroupChat
import ch.epfl.skysync.components.GroupDetail
import ch.epfl.skysync.navigation.BottomBar

@Composable
fun ChatScreen(navController: NavHostController) {
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("ChatScreenScaffold"),
      bottomBar = { BottomBar(navController) }) { padding ->
        val groupList =
            List(10) {
              GroupDetail(
                  groupName = "Group $it",
                  groupImage = null,
                  lastMessage = "Hello",
                  lastMessageTime = "12:00")
            }
        GroupChat(groupList = groupList, onClick = {}, paddingValues = padding)
      }
}

@Composable
@Preview
fun Preview() {
  val navController = rememberNavController()
  ChatScreen(navController = navController)
}
