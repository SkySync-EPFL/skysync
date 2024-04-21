package ch.epfl.skysync.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import ch.epfl.skysync.ui.components.Timer
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TimerTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    composeTestRule.setContent { Timer(modifier = Modifier) }
  }

  @Test
  fun timerIsDisplayed() {
    composeTestRule.onNodeWithTag("Timer").assertExists()
  }

  @Test
  fun timerValueIsDisplayed() {
    composeTestRule.onNodeWithTag("Timer Value").assertExists()
  }

  @Test
  fun timerStartButtonIsDisplayed() {
    composeTestRule.onNodeWithTag("Start Button").assertExists()
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun timerResetButtonIsDisplayed() {
    composeTestRule.onNodeWithTag("Timer Value").assertTextContains("00:00:00")
    composeTestRule.onNodeWithTag("Start Button").performClick()
    composeTestRule.waitUntilAtLeastOneExists(
        hasText("00:00:02", substring = true), timeoutMillis = 2010)
    composeTestRule.onNodeWithTag("Reset Button").assertExists()
  }

  @Test
  fun timerValueIsUpdated() {
    composeTestRule.onNodeWithTag("Start Button").performClick()
  }
}
