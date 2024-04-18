package ch.epfl.skysync.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SwitchButtonTest {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController

  @Before
  fun setUpNavHost() {
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      SwitchButton(Side.LEFT, PaddingValues(), "Text left", "Text Right", {}, {})
    }
  }

  @Test
  fun SwitchButtonFlightIsDisplayed() {
    composeTestRule.onNodeWithText("Text left").assertIsDisplayed()
  }

  @Test
  fun SwitchButtonFlightIsClickable() {
    composeTestRule.onNodeWithText("Text left").assertHasClickAction()
  }

  @Test
  fun SwitchButtonIsDisplayed() {
    composeTestRule.onNodeWithText("Text right").assertIsDisplayed()
  }

  @Test
  fun SwitchButtonIsClickable() {
    composeTestRule.onNodeWithText("Text right").assertHasClickAction()
  }
}
