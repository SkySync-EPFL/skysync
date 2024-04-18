package ch.epfl.skysync.screens.flight

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.screens.FlightScreen
import org.junit.Rule
import org.junit.Test

class FlightScreenNoPermissionTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun flightScreen_WithPermissionDenied_ShowsPermissionMessage() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      FlightScreen(navController)
    }

    composeTestRule
        .onNodeWithText("Access to location is required to use this feature.")
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithText("Please enable location permissions in settings.")
        .assertIsDisplayed()
  }
}
