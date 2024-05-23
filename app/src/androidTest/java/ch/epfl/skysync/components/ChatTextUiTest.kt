package ch.epfl.skysync.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.dp
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.models.message.ChatMessage
import ch.epfl.skysync.models.message.MessageType
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChatTextUiTest {
  @get:Rule val composeTestRule = createComposeRule()
  val dbs = DatabaseSetup()

  val image: ImageVector? = null
  private val fakeText =
      ChatMessage(
          dbs.message1,
          MessageType.RECEIVED,
          image,
      )
  private val myFakeText =
      ChatMessage(
          dbs.message2,
          MessageType.SENT,
          image,
      )
  private val list = (0..50).map { i -> if (i % 2 == 0) fakeText else myFakeText }

  @Before
  fun setUpNavHost() {
    composeTestRule.setContent {
      val context = LocalContext.current
      val connectivityStatus = remember { ContextConnectivityStatus(context) }
      ChatText(
          messages = list, onSend = {}, paddingValues = PaddingValues(0.dp), connectivityStatus)
    }
  }

  @Test
  fun verifyChatInput() {
    composeTestRule.onNodeWithTag("ChatInput").assertIsDisplayed()
  }

  @Test
  fun verifyChatTextBody() {
    composeTestRule.onNodeWithTag("ChatTextBody").assertIsDisplayed()
  }

  @Test
  fun verifyChatBubble() {
    for (index in list.indices) {
      composeTestRule
          .onNodeWithTag("ChatTextBody")
          .performScrollToNode(hasTestTag("ChatBubbleMessage$index"))
          .assertIsDisplayed()
    }
  }

  @Test
  fun verifyChatBubbleTime() {
    for (index in list.indices) {
      composeTestRule
          .onNodeWithTag("ChatTextBody")
          .performScrollToNode(hasTestTag("ChatBubbleTime$index"))
          .assertIsDisplayed()
    }
  }

  @Test
  fun verifyChatSendButton() {
    composeTestRule.onNodeWithTag("SendButton").assertIsDisplayed()
  }

  @Test
  fun verifyChatSendButtonIsClickable() {
    composeTestRule.onNodeWithTag("SendButton").assertHasClickAction()
  }
}
