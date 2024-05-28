package ch.epfl.skysync.screens.chat

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeWithVelocity
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.screens.admin.AdminChatScreen
import ch.epfl.skysync.screens.crewpilot.ChatScreen
import ch.epfl.skysync.viewmodel.ChatViewModel
import ch.epfl.skysync.viewmodel.MessageListenerViewModel
import org.junit.Rule
import org.junit.Test

class ChatScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController
  val dbs = DatabaseSetup()
  val db = FirestoreDatabase(useEmulator = true)
  val repository = Repository(db)

  @Test
  fun chatScreenIsDisplayed() {
    composeTestRule.setContent {
      val messageListenerViewModel = MessageListenerViewModel.createViewModel()
      val viewModel =
          ChatViewModel.createViewModel(
              uid = dbs.admin1.id, messageListenerViewModel, repository = repository)
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      ChatScreen(navController, viewModel)
    }
    composeTestRule.onNode(hasTestTag("ChatScreenScaffold")).assertIsDisplayed()
  }

  @Test
  fun AdminChatScreenIsDispayed() {
    composeTestRule.setContent {
      val messageListenerViewModel = MessageListenerViewModel.createViewModel()
      val viewModel =
          ChatViewModel.createViewModel(
              uid = dbs.admin1.id, messageListenerViewModel, repository = repository)
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      AdminChatScreen(navController, viewModel)
    }
    composeTestRule.onNode(hasTestTag("AdminChatScreenScaffold")).assertIsDisplayed()
  }

  @Test
  fun adminChatDeleteGroup() {
    composeTestRule.setContent {
      val messageListenerViewModel = MessageListenerViewModel.createViewModel()
      val viewModel =
          ChatViewModel.createViewModel(
              uid = dbs.admin1.id, messageListenerViewModel, repository = repository)
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      AdminChatScreen(navController, viewModel)
    }
    composeTestRule.onNode(hasTestTag("AdminChatScreenScaffold")).assertIsDisplayed()
    val before = composeTestRule.onAllNodes(hasTestTag("GroupCard")).fetchSemanticsNodes().size
    composeTestRule.onNode(hasTestTag("GroupCard0")).performTouchInput {
      swipeWithVelocity(Offset.Zero, Offset.Zero, 0f, 2000)
    }
    composeTestRule.onNodeWithTag("DeleteButton0").performClick()
    val after = composeTestRule.onAllNodes(hasTestTag("GroupCard")).fetchSemanticsNodes().size
    composeTestRule.waitForIdle()
    assert(before > after)
  }
}
