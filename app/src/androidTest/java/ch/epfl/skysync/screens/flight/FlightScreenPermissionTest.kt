package ch.epfl.skysync.screens.flight

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
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
  fun flightScreen_WithPermissionGranted_ShowsMapAndFAB() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      FlightScreen(navController)
    }

    composeTestRule.onNodeWithTag("Map").assertExists()
    composeTestRule.onNodeWithContentDescription("Locate Me").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Flight infos").performClick()
  }
}
