package ch.epfl.skysync.screens.flight

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import ch.epfl.skysync.screens.FlightScreen
import ch.epfl.skysync.viewmodel.TimerViewModel
import org.junit.Rule
import org.junit.Test

class FlightScreenNoPermissionTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun flightScreen_PermissionRequested_AndDenied() {

    val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    composeTestRule.setContent {
      val navController = rememberNavController()
      FlightScreen(navController, TimerViewModel.createViewModel())
    }

    val denyButton = uiDevice.findObject(UiSelector().text("Don’t allow"))
    if (denyButton.exists() && denyButton.isEnabled) {
      denyButton.click()
    }

    composeTestRule
        .onNodeWithText("Access to location is required to use this feature.")
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithText("Please enable location permissions in settings.")
        .assertIsDisplayed()
  }
}
