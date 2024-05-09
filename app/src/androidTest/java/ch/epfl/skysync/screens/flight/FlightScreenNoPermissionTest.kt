package ch.epfl.skysync.screens.flight

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.screens.crewpilot.FlightScreen
import ch.epfl.skysync.viewmodel.LocationViewModel
import ch.epfl.skysync.viewmodel.TimerViewModel
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
      val locationViewModel = LocationViewModel.createViewModel(dbs.pilot1.id, repository)
      val navController = rememberNavController()
      val uid = dbs.pilot1.id
      FlightScreen(
          navController = navController,
          TimerViewModel.createViewModel(),
          inFlightViewModel = locationViewModel,
          uid)
    }
  }

  @Test
  fun flightScreen_PermissionRequested_AndDenied() {

    val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

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
