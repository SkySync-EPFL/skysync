package ch.epfl.skysync.screens.flight

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.rule.GrantPermissionRule
import ch.epfl.skysync.screens.FlightScreen
import org.junit.Rule
import org.junit.Test

class FlightScreenPermissionTest {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  var permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  @Test
  fun flightScreen_PermissionGranted_ShowsMapAndFAB() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      FlightScreen(navController)
    }

    composeTestRule.onNodeWithTag("LoadingIndicator").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("Timer").assertExists()
    composeTestRule.onNodeWithTag("Map").assertExists()
    composeTestRule.onNodeWithContentDescription("Locate Me").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Flight infos").performClick()
    composeTestRule
        .onNodeWithText("X Speed: 0.0 m/s\nY Speed: 0.0 m/s\nAltitude: 0.0 m\nBearing: 0.0 °")
        .assertIsDisplayed()
  }
}
