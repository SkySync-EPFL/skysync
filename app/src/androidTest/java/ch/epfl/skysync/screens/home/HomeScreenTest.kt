package ch.epfl.skysync.screens.home

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.screens.crewpilot.HomeScreen
import ch.epfl.skysync.viewmodel.FlightsViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController
  lateinit var flightsViewModel: FlightsViewModel
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  private val repository = Repository(db)

  @Before
  fun setUpNavHost() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
  }

  @Test
  fun crewAndPilotHasNoButton() {
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      flightsViewModel = FlightsViewModel.createViewModel(repository, dbs.crew1.id)
      HomeScreen(navController = navController, viewModel = flightsViewModel)
    }
    runTest {
      flightsViewModel.refreshUserAndFlights().join()
      composeTestRule.onNodeWithTag("addFlightButton").assertDoesNotExist()
    }
  }
}
