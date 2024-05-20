package ch.epfl.skysync.screens.crewpilot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

@Composable
fun ChatScreen(navController: NavHostController, viewModel: ChatViewModel) {
    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("ChatScreenScaffold"),
        bottomBar = { BottomBar(navController) },
        floatingActionButton = {
            IconButton(onClick = { navController.navigate(Route.CREW_REPORT) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Report")
            }
        }
    ) { padding ->
        val groupDetails by viewModel.getGroupDetails().collectAsStateWithLifecycle()
        val sortedGroups = groupDetails.sortedByDescending { it.lastMessage?.date }
        GroupChat(
            groupList = sortedGroups,
            onClick = { selectedGroup ->
                navController.navigate(Route.CREW_TEXT + "/${selectedGroup.id}")
            },
            paddingValues = padding
        )
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
