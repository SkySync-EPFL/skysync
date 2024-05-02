package ch.epfl.skysync.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class TimerTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun timerIsDisplayedWithWithButton() {
    composeTestRule.setContent {
      Timer(
          modifier = Modifier, currentTimer = "0:0:0", isRunning = false, onStart = {}, onStop = {})
    }
    composeTestRule.onNodeWithTag("Timer").assertExists()
    composeTestRule.onNodeWithTag("Timer Value").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Start Button").assertIsDisplayed()
    composeTestRule.onNodeWithText("0:0:0").assertIsDisplayed()
  }

  @Test
  fun correctButtonIsDisplayedAndClickableIfPaused() {
    var controlVariableOnStart = false
    var controlVariableOnStop = false
    composeTestRule.setContent {
      Timer(
          modifier = Modifier,
          currentTimer = "0:0:0",
          isRunning = false,
          onStart = { controlVariableOnStart = true },
          onStop = { controlVariableOnStop = true })
    }
    composeTestRule.onNodeWithTag("Start Button").performClick()
    assert(controlVariableOnStart)
    assertFalse(controlVariableOnStop)
  }

  @Test
  fun correctButtonIsDisplayedAndClickableIfRunning() {
    var controlVariableOnStart = false
    var controlVariableOnStop = false
    composeTestRule.setContent {
      Timer(
          modifier = Modifier,
          currentTimer = "0:0:0",
          isRunning = true,
          onStart = { controlVariableOnStart = true },
          onStop = { controlVariableOnStop = true })
    }
    composeTestRule.onNodeWithTag("Stop Button").performClick()
    assert(controlVariableOnStop)
    assertFalse(controlVariableOnStart)
  }
}
