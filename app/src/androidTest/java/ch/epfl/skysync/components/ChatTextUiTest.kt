package ch.epfl.skysync.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.dp
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChatTextUiTest {
  @get:Rule val composeTestRule = createComposeRule()
  val image: ImageVector? = null
  private val fakeText = Pair(Pair("him", image), Pair("Hi", "11:11"))
  private val myFakeText = Pair(Pair("me", image), Pair("Hello", "12:12"))
  private val list =
      listOf(
          fakeText,
          myFakeText,
          fakeText,
          myFakeText,
          fakeText,
          myFakeText,
          fakeText,
          myFakeText,
          fakeText,
          myFakeText,
          fakeText,
          myFakeText,
          fakeText,
          myFakeText,
          fakeText,
          myFakeText,
          fakeText,
          myFakeText,
          fakeText,
          myFakeText,
          fakeText,
          myFakeText,
          fakeText,
          myFakeText,
          fakeText,
          myFakeText,
          fakeText,
          myFakeText,
          fakeText,
          myFakeText,
          fakeText,
          myFakeText,
          fakeText,
          myFakeText,
          fakeText,
          myFakeText,
          fakeText,
          myFakeText,
          fakeText,
          myFakeText,
          fakeText,
          myFakeText,
          fakeText,
          myFakeText,
      )

  @Before
  fun setUpNavHost() {
    composeTestRule.setContent {
      ChatText(
          groupName = "Name",
          ListofPairSenderImagePairMsgTime = list,
          backClick = {},
          sendClick = {},
          paddingValues = PaddingValues(0.dp))
    }
  }

  @Test
  fun verifyChatText() {
    composeTestRule.onNodeWithTag("HeaderTitle").assertIsDisplayed()
  }

  @Test
  fun verifyChatBackButton() {
    composeTestRule.onNodeWithTag("BackButton").assertIsDisplayed()
  }

  @Test
  fun verifyChatBackButtonIsClickable() {
    composeTestRule.onNodeWithTag("BackButton").assertHasClickAction()
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
