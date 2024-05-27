package ch.epfl.skysync.screens.flight

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import androidx.test.rule.GrantPermissionRule
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.screens.crewpilot.FlightScreen
import ch.epfl.skysync.viewmodel.InFlightViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FlightScreenPermissionTest {
  @get:Rule val composeTestRule = createComposeRule()

  lateinit var inFlightViewModel: InFlightViewModel
  lateinit var dbs: DatabaseSetup

  @get:Rule
  var permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  @Before
  fun setUpNavHost() = runTest {
    val db = FirestoreDatabase(useEmulator = true)
    dbs = DatabaseSetup()
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
    val repository = Repository(db)
    val uid = dbs.pilot1.id
    composeTestRule.setContent {
      inFlightViewModel = InFlightViewModel.createViewModel(repository)
      val navController = rememberNavController()
      FlightScreen(navController, inFlightViewModel = inFlightViewModel, uid)
    }
    inFlightViewModel.init(dbs.pilot1.id).join()
    inFlightViewModel.setCurrentFlight(dbs.flight4.id)
  }

  @Test
  fun flightScreen_PermissionGranted_ShowsMapAndFAB() = runTest {
    composeTestRule.onNodeWithTag("LoadingIndicator").assertIsNotDisplayed()
    composeTestRule.waitUntil(timeoutMillis = 3000) {
      composeTestRule.onNodeWithTag("Timer").isDisplayed()
    }
    composeTestRule.onNodeWithTag("Map").assertExists()
    composeTestRule.onNodeWithContentDescription("Locate Me").assertIsDisplayed()
    composeTestRule
        .onNodeWithText(
            "Horizontal Speed: 0.00 m/s\nVertical Speed: 0.00 m/s\nAltitude: 0 m\nBearing: 0.00 °")
        .assertIsDisplayed()
  }
}
