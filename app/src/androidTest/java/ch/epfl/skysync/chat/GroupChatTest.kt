package ch.epfl.skysync.chat

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeWithVelocity
import androidx.compose.ui.unit.dp
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.components.GroupChat
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.models.flight.FlightColor
import ch.epfl.skysync.models.message.GroupDetails
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GroupChatTest {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController
  val dbs = DatabaseSetup()
  val group = GroupDetails("id1", "Group", FlightColor.NO_COLOR, dbs.message1)
  val searchGroup = GroupDetails("id2", "GroupSearch", FlightColor.GREEN, dbs.message2)
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
          groupList = groups,
          onClick = {},
          onDelete = {},
          paddingValues = PaddingValues(0.dp),
          isAdmin = true)
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

  @Test
  fun testDeleteButtonIsDisplayed() {
    for (i in groups.indices) {
      composeTestRule.onNodeWithTag("GroupChatBody").performScrollToNode(hasTestTag("GroupCard$i"))
      composeTestRule.onNodeWithTag("GroupCard$i").performTouchInput {
        swipeWithVelocity(Offset.Zero, Offset.Zero, 0f, 2000)
      }
      composeTestRule
          .onNodeWithTag("GroupChatBody")
          .performScrollToNode(hasTestTag("DeleteButton$i"))
      composeTestRule.onNodeWithTag("DeleteButton$i").assertIsDisplayed()
      composeTestRule.onNodeWithTag("DeleteButton$i").performClick()
    }
  }
}
