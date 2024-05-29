package ch.epfl.skysync.screens.flight

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.screens.admin.AdminFlightScreen
import ch.epfl.skysync.viewmodel.InFlightViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AdminFlightScreenNoPermissionTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUpNavHost() {
    val db = FirestoreDatabase(useEmulator = true)
    val repository = Repository(db)
    composeTestRule.setContent {
      val inFlightViewModel = InFlightViewModel.createViewModel(repository)
      val navController = rememberNavController()
      AdminFlightScreen(navController = navController, inFlightViewModel = inFlightViewModel)
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
