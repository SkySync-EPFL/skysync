package ch.epfl.skysync.chat

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.components.GroupChat
import ch.epfl.skysync.components.GroupDetail
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GroupChatTest {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController
  val image: ImageVector? = null
  val group = GroupDetail("Group", image, "Last message", "Last message time")
  val searchGroup = GroupDetail("GroupSearch", image, "Last message", "Last message time")
  val groups =
      listOf(
          group,
          group,
          group,
          group,
          group,
          group,
          group,
          group,
          group,
          group,
          group,
          group,
          group,
          group,
          group,
          group,
          group,
          searchGroup)

  @Before
  fun setUpNavHost() {
    composeTestRule.setContent {
      GroupChat(
          groupList = groups, onClick = {}, paddingValues = PaddingValues(0.dp))
    }
  }

  @Test
  fun testGroupSearchIsDisplayed() {
    composeTestRule.onNodeWithTag("Search").assertIsDisplayed()
  }

  @Test
  fun testGroupCardIsDisplayed() {
    for (i in groups.indices) {
      composeTestRule.onNodeWithTag("GroupChatBody").performScrollToNode(hasTestTag("GroupCard$i"))
      composeTestRule.onNodeWithTag("GroupCard$i").assertIsDisplayed()
    }
  }

  @Test
  fun testGroupCardIsClickable() {
    for (i in groups.indices) {
      composeTestRule.onNodeWithTag("GroupChatBody").performScrollToNode(hasTestTag("GroupCard$i"))
      composeTestRule.onNodeWithTag("GroupCard$i").assertHasClickAction()
    }
  }

  @Test
  fun testGroupCardIsDisplayedAfterSearch() {
    composeTestRule.onNodeWithTag("Search").performTextInput("Search")
    composeTestRule.onNodeWithText("GroupSearch").assertIsDisplayed()
  }
}
