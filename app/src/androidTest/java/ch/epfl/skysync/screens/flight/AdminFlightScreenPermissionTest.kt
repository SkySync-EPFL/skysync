package ch.epfl.skysync.screens.flight

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import androidx.test.rule.GrantPermissionRule
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.screens.admin.AdminFlightScreen
import ch.epfl.skysync.viewmodel.InFlightViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AdminFlightScreenPermissionTest {
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
    composeTestRule.setContent {
      inFlightViewModel = InFlightViewModel.createViewModel(repository)
      val navController = rememberNavController()
      AdminFlightScreen(navController, inFlightViewModel = inFlightViewModel)
    }
    inFlightViewModel.init(dbs.pilot1.id).join()
    inFlightViewModel.setCurrentFlight(dbs.flight4.id)
  }

  @Test
  fun flightScreen_PermissionGranted_ShowsMapAndFAB() = runTest {
    composeTestRule.onNodeWithTag("Map").assertExists()
    composeTestRule.onNodeWithTag("Quit display").assertExists()
  }
}
