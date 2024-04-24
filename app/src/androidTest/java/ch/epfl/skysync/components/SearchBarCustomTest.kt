package ch.epfl.skysync.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import ch.epfl.skysync.components.forms.SearchBarCustom
import ch.epfl.skysync.models.flight.RoleType
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchBarCustomTest {
  @get:Rule val composeTestRule = createComposeRule()
  private val propositions = RoleType.entries

  @Before
  fun setUp() {
    composeTestRule.setContent {
      var query by remember { mutableStateOf("") }
      var active by remember { mutableStateOf(false) }

      SearchBarCustom(
          query = query,
          onQueryChange = { query = it },
          onSearch = { active = false },
          active = active,
          onActiveChange = { active = it },
          onElementClick = {
            active = false
            query = it.toString()
          },
          propositions = propositions,
          showProposition = { it.toString() },
          placeholder = "Test Name")
    }
  }

  @Test
  fun isPlaceHolderDisplayed() {
    composeTestRule.onNodeWithTag("Search Bar Input").assertTextContains("Test Name")
  }

  @Test
  fun doesSearchBarActiveWorks() {
    composeTestRule.onNodeWithTag("Search Bar Input").performClick()
    composeTestRule.onNodeWithText(propositions[0].toString()).performClick()
    composeTestRule.onNodeWithTag("Search Propositions").assertIsNotDisplayed()
  }

  @Test
  fun doesQueryWorksWell() {
    composeTestRule.onNodeWithTag("Search Bar Input").performClick()
    composeTestRule.onNodeWithText(propositions[0].toString()).performClick()
    composeTestRule.onNodeWithTag("Search Bar Input").assertTextContains(propositions[0].toString())

    composeTestRule.onNodeWithTag("Search Bar Input").performClick()
    composeTestRule.onNodeWithTag("Search Bar Input").performTextClearance()
    composeTestRule.onNodeWithTag("Search Bar Input").assertTextContains("")
    composeTestRule.onNodeWithTag("Search Bar Input").performTextInput("Test")
    composeTestRule.onNodeWithTag("Search Bar Input").assertTextContains("Test")
  }
}
