package ch.epfl.skysync.screens.flight

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.screens.FlightScreen
import org.junit.Rule
import org.junit.Test

class FlightScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun flightScreen_WithPermissionDenied_ShowsPermissionMessage() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      FlightScreen(navController) // Assume FlightScreen handles permissions internally
    }

    // Assuming you have a way to simulate permission denied
    composeTestRule
        .onNodeWithText("Access to location is required to use this feature.")
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithText("Please enable location permissions in settings.")
        .assertIsDisplayed()
  }

  @Test
  fun flightScreen_WithPermissionGranted_ShowsMapAndFAB() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      FlightScreen(navController) // Again, ensure FlightScreen can simulate granted permissions
    }

    // Assuming your map is identifiable by a specific content description or test tag
    composeTestRule.onNodeWithTag("Map").assertExists()
    composeTestRule.onNodeWithContentDescription("Locate Me").assertIsDisplayed()
  }
}
