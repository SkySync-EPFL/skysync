package ch.epfl.skysync.screens.flight

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.screens.FlightScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FlightScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController

  @Before
  fun setUpNavHost() {
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      FlightScreen(navController)
    }
  }

  @Test
  fun flightScreenIsDisplayed() {
    composeTestRule.onNode(hasTestTag("FLightScreenScaffold")).assertIsDisplayed()
  }
}