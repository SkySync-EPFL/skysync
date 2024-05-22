package ch.epfl.skysync.screens.home

import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.ContextConnectivityStatus
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.screens.admin.AdminHomeScreen
import ch.epfl.skysync.viewmodel.FlightsViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AdminHomeScreenTest {
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
  fun adminHasButton() {
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      flightsViewModel = FlightsViewModel.createViewModel(repository, dbs.admin1.id)
      val context = LocalContext.current
      val connectivityStatus = remember { ContextConnectivityStatus(context) }
      AdminHomeScreen(
          navController = navController, viewModel = flightsViewModel, connectivityStatus)
    }
    runTest {
      flightsViewModel.refreshUserAndFlights().join()
      composeTestRule.onNodeWithTag("addFlightButton").assertIsDisplayed()
    }
  }
}
