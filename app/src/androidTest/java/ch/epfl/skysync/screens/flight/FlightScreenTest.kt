package ch.epfl.skysync.screens.flight

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.screens.ChatScreen
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
      ChatScreen(navController)
    }
  }

  @Test
  fun flightScreenIsDisplayed() {
    composeTestRule.onNode(hasTestTag("FLightScreenScaffold\"")).assertHasClickAction()
  }
}
