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
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.screens.crewpilot.FlightScreen
import ch.epfl.skysync.viewmodel.LocationViewModel
import ch.epfl.skysync.viewmodel.TimerViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FlightScreenPermissionTest {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  var permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  @Before
  fun setUpNavHost() {
    val dbs = DatabaseSetup()
    val db = FirestoreDatabase(useEmulator = true)
    val repository = Repository(db)
    composeTestRule.setContent {
      val locationViewModel = LocationViewModel.createViewModel(repository)
      val navController = rememberNavController()
      val uid = dbs.pilot1.id
      FlightScreen(
          navController,
          TimerViewModel.createViewModel(),
          locationViewModel = locationViewModel,
          uid)
    }
  }

  @Test
  fun flightScreen_PermissionGranted_ShowsMapAndFAB() {

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
