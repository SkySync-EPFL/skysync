package ch.epfl.skysync.screens.flight

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.screens.crewpilot.FlightScreen
import ch.epfl.skysync.viewmodel.InFlightViewModel
import org.junit.Before
import org.junit.Rule

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
}
