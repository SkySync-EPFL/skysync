package ch.epfl.skysync.screens.reports

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.utils.inputTimePicker
import ch.epfl.skysync.viewmodel.FinishedFlightsViewModel
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PilotReportScreenTest {
  @get:Rule val composeTestRule = createComposeRule()
  private val navController: NavHostController = mockk(relaxed = true)
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  private val repository = Repository(db)
  private lateinit var finishedFlightsViewModel: FinishedFlightsViewModel

  @Before
  fun setUp() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
    composeTestRule.setContent {
      finishedFlightsViewModel =
          FinishedFlightsViewModel.createViewModel(repository = repository, userId = dbs.pilot1.id)
      PilotReportScreen(
          navHostController = navController,
          finishedFlightsViewModel = finishedFlightsViewModel,
          flightId = dbs.finishedFlight1.id)
    }
    finishedFlightsViewModel.refreshUserAndFlights().join()
  }

  @Test
  fun allFieldsAreDisplayed() {
    composeTestRule
        .onNodeWithTag("Pilot Report LazyColumn")
        .performScrollToNode(hasTestTag("Number of passengers"))
    composeTestRule.onNodeWithTag("Number of passengers").assertExists()

    composeTestRule
        .onNodeWithTag("Pilot Report LazyColumn")
        .performScrollToNode(hasTestTag("Takeoff time"))
    composeTestRule.onNodeWithTag("Takeoff time").assertExists()

    composeTestRule
        .onNodeWithTag("Pilot Report LazyColumn")
        .performScrollToNode(hasTestTag("Takeoff location Search Bar Input"))
    composeTestRule.onNodeWithTag("Takeoff location Search Bar Input").assertExists()

    composeTestRule
        .onNodeWithTag("Pilot Report LazyColumn")
        .performScrollToNode(hasTestTag("Landing time"))
    composeTestRule.onNodeWithTag("Landing time").assertExists()

    composeTestRule
        .onNodeWithTag("Pilot Report LazyColumn")
        .performScrollToNode(hasTestTag("Landing location Search Bar Input"))
    composeTestRule.onNodeWithTag("Landing location Search Bar Input").assertExists()

    composeTestRule
        .onNodeWithTag("Pilot Report LazyColumn")
        .performScrollToNode(hasTestTag("Effective time of start"))
    composeTestRule.onNodeWithTag("Effective time of start").assertExists()

    composeTestRule
        .onNodeWithTag("Pilot Report LazyColumn")
        .performScrollToNode(hasTestTag("Effective time of end"))
    composeTestRule.onNodeWithTag("Effective time of end").assertExists()

    composeTestRule
        .onNodeWithTag("Pilot Report LazyColumn")
        .performScrollToNode(hasTestTag("Pause duration"))
    composeTestRule.onNodeWithTag("Pause duration").assertExists()

    composeTestRule
        .onNodeWithTag("Pilot Report LazyColumn")
        .performScrollToNode(hasTestTag("Comments"))
    composeTestRule.onNodeWithTag("Comments").assertExists()

    composeTestRule.onNodeWithTag("Submit Button").assertExists()
  }

  @Test
  fun minimalReportWorks() {
    composeTestRule
        .onNodeWithTag("Pilot Report LazyColumn")
        .performScrollToNode(hasTestTag("Number of passengers"))
    composeTestRule.onNodeWithTag("Number of passengers").performTextClearance()
    composeTestRule.onNodeWithTag("Number of passengers").performTextInput("1")

    composeTestRule
        .onNodeWithTag("Pilot Report LazyColumn")
        .performScrollToNode(hasTestTag("Effective time of start"))
    composeTestRule.onNodeWithTag("Effective time of start").performClick()
    inputTimePicker(composeTestRule, 12, 50)

    composeTestRule
        .onNodeWithTag("Pilot Report LazyColumn")
        .performScrollToNode(hasTestTag("Effective time of end"))
    composeTestRule.onNodeWithTag("Effective time of end").performClick()
    inputTimePicker(composeTestRule, 14, 40)

    composeTestRule
        .onNodeWithTag("Pilot Report LazyColumn")
        .performScrollToNode(hasTestTag("Pause duration"))
    composeTestRule.onNodeWithTag("Pause duration").performClick()
    composeTestRule.onNodeWithTag("HourCircularList").performScrollToNode(hasText("00"))
    composeTestRule.onNodeWithTag("MinuteCircularList").performScrollToNode(hasText("30"))

    composeTestRule.onNodeWithTag("AlertDialogConfirm").performClick()

    composeTestRule.onNodeWithTag("Submit Button").performClick()
  }
}
