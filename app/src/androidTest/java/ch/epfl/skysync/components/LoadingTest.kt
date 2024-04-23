package ch.epfl.skysync.components

import androidx.compose.material.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class LoadingTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun loadingComponent_ShouldDisplayProgressIndicator_WhenIsLoadingIsTrue() {
    composeTestRule.setContent {
      LoadingComponent(isLoading = true, onRefresh = {}) { Text("Content goes here") }
    }

    composeTestRule.onNodeWithTag("LoadingIndicator").assertIsDisplayed()
  }

  @Test
  fun loadingComponent_ShouldNotDisplayProgressIndicator_WhenIsLoadingIsFalse() {
    composeTestRule.setContent {
      LoadingComponent(isLoading = false, onRefresh = {}) { Text("Content goes here") }
    }

    composeTestRule.onNodeWithContentDescription("Progress Indicator").assertDoesNotExist()
  }

  @Test
  fun loadingComponent_ShouldDisplayContent() {
    composeTestRule.setContent {
      LoadingComponent(isLoading = false, onRefresh = {}) { Text("Test Content") }
    }

    composeTestRule.onNodeWithText("Test Content").assertIsDisplayed()
  }
}
