package ch.epfl.skysync.screens.userDetails

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.screens.UserDetailsScreen
import ch.epfl.skysync.viewmodel.FlightsViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserDetailsTest {
  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var flightsViewModel: FlightsViewModel

  private val dbs = DatabaseSetup()
  private val db = FirestoreDatabase(useEmulator = true)
  private val repository: Repository = Repository(db)

  private lateinit var navController: NavHostController

  @Before
  fun setUp() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testPersonalFlightHistoryDisplaysFlights() = runTest {
    // Check for elements
    composeTestRule.setContent {
      flightsViewModel = FlightsViewModel.createViewModel(repository, dbs.crew1.id)
      UserDetailsScreen(navController = rememberNavController(), flightsViewModel)
    }
    flightsViewModel.refreshUserAndFlights().join()
    composeTestRule.onNodeWithText("Completed Flights").assertIsDisplayed()
    composeTestRule.waitUntilAtLeastOneExists(hasTestTag("flightCard${dbs.flight1.id}"), 3000)
  }

  @Test
  fun testEmptyMessage() {
    composeTestRule.setContent {
      flightsViewModel = FlightsViewModel.createViewModel(repository, dbs.pilot3.id)
      flightsViewModel.refresh()
      UserDetailsScreen(navController = rememberNavController(), flightsViewModel)
    }
    composeTestRule.waitUntil(4000) { flightsViewModel.currentUser.value != null }
    composeTestRule.onNodeWithText("No flights").assertIsDisplayed()
  }

  @Test
  fun testUserNameIsDisplayed() {
    composeTestRule.setContent {
      flightsViewModel = FlightsViewModel.createViewModel(repository, dbs.crew1.id)
      flightsViewModel.refresh()
      UserDetailsScreen(navController = rememberNavController(), flightsViewModel)
    }
    composeTestRule
        .onNodeWithText("${dbs.crew1.firstname} ${dbs.crew1.lastname}")
        .assertIsDisplayed()
  }
}
