package ch.epfl.skysync.screens.flight

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.screens.crewpilot.FlightScreen
import ch.epfl.skysync.viewmodel.InFlightViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FlightScreenNoPermissionTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUpNavHost() {
    val dbs = DatabaseSetup()
    val db = FirestoreDatabase(useEmulator = true)
    val repository = Repository(db)
    composeTestRule.setContent {
      val inFlightViewModel = InFlightViewModel.createViewModel(repository)
      val navController = rememberNavController()
      val uid = dbs.pilot1.id
      FlightScreen(navController = navController, inFlightViewModel = inFlightViewModel, uid)
    }
  }

  @Test
  fun flightScreen_PermissionRequested_AndDenied() {

    val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    try {
      // First, try to find the button directly
      var denyButton = uiDevice.findObject(UiSelector().text("Don’t allow"))

      // If the button is not found, try scrolling to find it
      if (!denyButton.exists()) {
        val scrollable = UiScrollable(UiSelector().scrollable(true))

        if (scrollable.exists()) {
          scrollable.scrollIntoView(UiSelector().text("Don’t allow"))
        }

        // Attempt to find the button again after scrolling
        denyButton = uiDevice.findObject(UiSelector().text("Don’t allow"))
      }

      // Click the button if found
      if (denyButton.exists() && denyButton.isEnabled) {
        denyButton.click()
      } else {
        // Handle the case where the button is not found or not enabled
        println("Deny button not found or not enabled.")
      }
    } catch (e: UiObjectNotFoundException) {
      // Handle the exception as needed
      e.printStackTrace()
    }

    composeTestRule
        .onNodeWithText("Access to location is required to use this feature.")
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithText("Please enable location permissions in settings.")
        .assertIsDisplayed()
  }
}
