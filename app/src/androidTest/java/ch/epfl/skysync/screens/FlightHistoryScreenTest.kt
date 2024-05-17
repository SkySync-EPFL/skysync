package ch.epfl.skysync.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.screens.admin.FlightHistoryScreen
import ch.epfl.skysync.viewmodel.FinishedFlightsViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FlightHistoryScreenTest {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: NavHostController
  private lateinit var finishedFlightsViewModel: FinishedFlightsViewModel
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbSetup = DatabaseSetup()
  private val repository: Repository = Repository(db)

  @Before
  fun setupHistory() = runTest {
    dbSetup.clearDatabase(db)
    dbSetup.fillDatabase(db)
  }

  @Test
  fun filtersMenuAppearCorrectly() = runTest {
    composeTestRule.setContent {
      navController = rememberNavController()
      finishedFlightsViewModel =
          FinishedFlightsViewModel.createViewModel(repository, dbSetup.admin1.id)
      finishedFlightsViewModel.refresh()
      FlightHistoryScreen(navController, finishedFlightsViewModel)
    }

    composeTestRule.waitUntil { finishedFlightsViewModel.currentFlights.value != null }
    composeTestRule.onNodeWithTag("Filter Button").performClick()
    composeTestRule.onNodeWithTag("Filter Menu").assertIsDisplayed()
  }

  @Test
  fun twoCardAreInitiallyDisplayed() {
    composeTestRule.setContent {
      navController = rememberNavController()
      finishedFlightsViewModel =
          FinishedFlightsViewModel.createViewModel(repository, dbSetup.admin1.id)
      finishedFlightsViewModel.refresh()
      FlightHistoryScreen(navController, finishedFlightsViewModel)
    }
    composeTestRule.waitUntil(2000) { finishedFlightsViewModel.currentFlights.value != null }
    composeTestRule.onNodeWithTag("Card 0").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Card 1").assertIsDisplayed()
  }

  @Test
  fun searchBarWorksCorrectly() {
    // TODO finish the implementation of the search bar and of the test
    composeTestRule.setContent {
      navController = rememberNavController()
      finishedFlightsViewModel =
          FinishedFlightsViewModel.createViewModel(repository, dbSetup.admin1.id)
      finishedFlightsViewModel.refresh()
      FlightHistoryScreen(navController, finishedFlightsViewModel)
    }
    composeTestRule.waitUntil { finishedFlightsViewModel.currentFlights.value != null }
    composeTestRule.onNodeWithTag("Search Bar").onChildAt(0).performTextInput("Lausanne 1")
    // Not implemented yet
    /*composeTestRule.onNodeWithTag("Card 0").assertExists()
    composeTestRule.onNodeWithTag("Card 1").assertDoesNotExist()*/
  }

  @Test
  fun rangeDateSelectorShowsWorksCorrectly() {
    composeTestRule.setContent {
      navController = rememberNavController()
      finishedFlightsViewModel =
          FinishedFlightsViewModel.createViewModel(repository, dbSetup.admin1.id)
      finishedFlightsViewModel.refresh()
      FlightHistoryScreen(navController, finishedFlightsViewModel)
    }
    composeTestRule.waitUntil { finishedFlightsViewModel.currentFlights.value != null }
    composeTestRule.onNodeWithTag("Filter Button").performClick()
    composeTestRule.onNodeWithTag("Date Range Field 1").performClick()
    composeTestRule.onNodeWithTag("Date Range Selector").assertIsDisplayed()
  }

  @Test
  fun noFlightIsCorrectlyDisplayed() {
    composeTestRule.setContent {
      navController = rememberNavController()
      finishedFlightsViewModel =
          FinishedFlightsViewModel.createViewModel(repository, dbSetup.pilot3.id)
      finishedFlightsViewModel.refresh()
      FlightHistoryScreen(navController, finishedFlightsViewModel)
    }
    composeTestRule.waitUntil { finishedFlightsViewModel.currentFlights.value != null }
    composeTestRule.onNodeWithTag("No Flight").assertIsDisplayed()
  }
}
