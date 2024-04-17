package ch.epfl.skysync

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.components.SwitchButton
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
      SwitchButton(Availability = true, padding = PaddingValues(), {}, {})
    }
  }

  @Test
  fun SwitchButtonFlightIsDisplayed() {
    composeTestRule.onNodeWithText("Flight Calendar").assertIsDisplayed()
  }

  @Test
  fun SwitchButtonFlightIsClickable() {
    composeTestRule.onNodeWithText("Flight Calendar").assertHasClickAction()
  }

  @Test
  fun SwitchButtonIsDisplayed() {
    composeTestRule.onNodeWithText("Availability Calendar").assertIsDisplayed()
  }

  @Test
  fun SwitchButtonIsClickable() {
    composeTestRule.onNodeWithText("Availability Calendar").assertHasClickAction()
  }
}
