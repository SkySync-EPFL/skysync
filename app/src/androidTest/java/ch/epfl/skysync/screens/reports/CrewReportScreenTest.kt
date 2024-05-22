package ch.epfl.skysync.screens.reports

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
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

class CrewReportScreenTest {
  @get:Rule val composeTestRule = createComposeRule()
  private val navController: NavHostController = mockk(relaxed = true)
  private lateinit var finishedFlightsViewModel: FinishedFlightsViewModel
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  private val repository = Repository(db)

  @Before
  fun setUp() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
    composeTestRule.setContent {
      finishedFlightsViewModel =
          FinishedFlightsViewModel.createViewModel(repository = repository, userId = dbs.crew1.id)
      CrewReportScreen(
          navHostController = navController,
          finishedFlightsViewModel = finishedFlightsViewModel,
          flightId = dbs.finishedFlight1.id)
    }
    finishedFlightsViewModel.refreshAndSelectFlight(dbs.finishedFlight1.id).join()
  }

  @Test
  fun allFieldsAreDisplayed() {
    composeTestRule
        .onNodeWithTag("Crew Report LazyColumn")
        .performScrollToNode(hasTestTag("Number of little champagne bottles"))
    composeTestRule.onNodeWithTag("Number of little champagne bottles").assertExists()

    composeTestRule
        .onNodeWithTag("Crew Report LazyColumn")
        .performScrollToNode(hasTestTag("Number of big champagne bottles"))
    composeTestRule.onNodeWithTag("Number of big champagne bottles").assertExists()

    composeTestRule
        .onNodeWithTag("Crew Report LazyColumn")
        .performScrollToNode(hasTestTag("Number of prestige champagne bottles"))
    composeTestRule.onNodeWithTag("Number of prestige champagne bottles").assertExists()

    composeTestRule
        .onNodeWithTag("Crew Report LazyColumn")
        .performScrollToNode(hasTestTag("Effective time of start"))
    composeTestRule.onNodeWithTag("Effective time of start").assertExists()

    composeTestRule
        .onNodeWithTag("Crew Report LazyColumn")
        .performScrollToNode(hasTestTag("Effective time of end"))
    composeTestRule.onNodeWithTag("Effective time of end").assertExists()

    composeTestRule
        .onNodeWithTag("Crew Report LazyColumn")
        .performScrollToNode(hasTestTag("Pause duration"))
    composeTestRule.onNodeWithTag("Pause duration").assertExists()

    composeTestRule
        .onNodeWithTag("Crew Report LazyColumn")
        .performScrollToNode(hasTestTag("Comments"))
    composeTestRule.onNodeWithTag("Comments").assertExists()

    composeTestRule.onNodeWithTag("Submit Button").assertExists()
  }

  @Test
  fun minimalReportWorks() {
    composeTestRule
        .onNodeWithTag("Crew Report LazyColumn")
        .performScrollToNode(hasTestTag("Number of little champagne bottles"))
    composeTestRule.onNodeWithTag("Number of little champagne bottles").performTextClearance()
    composeTestRule.onNodeWithTag("Number of little champagne bottles").performTextInput("0")

    composeTestRule
        .onNodeWithTag("Crew Report LazyColumn")
        .performScrollToNode(hasTestTag("Number of big champagne bottles"))
    composeTestRule.onNodeWithTag("Number of big champagne bottles").performTextClearance()
    composeTestRule.onNodeWithTag("Number of big champagne bottles").performTextInput("0")

    composeTestRule
        .onNodeWithTag("Crew Report LazyColumn")
        .performScrollToNode(hasTestTag("Number of prestige champagne bottles"))
    composeTestRule.onNodeWithTag("Number of prestige champagne bottles").performTextClearance()
    composeTestRule.onNodeWithTag("Number of prestige champagne bottles").performTextInput("1")

    composeTestRule
        .onNodeWithTag("Crew Report LazyColumn")
        .performScrollToNode(hasTestTag("Effective time of start"))
    composeTestRule.onNodeWithTag("Effective time of start").performClick()
    inputTimePicker(composeTestRule, 12, 50)

    composeTestRule
        .onNodeWithTag("Crew Report LazyColumn")
        .performScrollToNode(hasTestTag("Effective time of end"))
    composeTestRule.onNodeWithTag("Effective time of end").performClick()
    inputTimePicker(composeTestRule, 14, 40)

    composeTestRule
        .onNodeWithTag("Crew Report LazyColumn")
        .performScrollToNode(hasTestTag("Pause duration"))
    composeTestRule.onNodeWithTag("Pause duration").performClick()
    composeTestRule.onNodeWithTag("HourCircularList").performScrollToNode(hasText("00"))
    composeTestRule.onNodeWithTag("MinuteCircularList").performScrollToNode(hasText("30"))
    composeTestRule.onNodeWithText("Confirm", true).performClick()

    composeTestRule
        .onNodeWithTag("Crew Report LazyColumn")
        .performScrollToNode(hasTestTag("Comments"))
    composeTestRule.onNodeWithTag("Add Problem Button").performClick()
    composeTestRule.onNodeWithTag("Vehicle Menu").performClick()
    composeTestRule.onNodeWithTag("Vehicle 1").performClick()
    composeTestRule.onNodeWithTag("Problem Field").performTextInput("Problem 1")
    composeTestRule.onNodeWithTag("Add Vehicle Problem Button").performClick()

    composeTestRule.onNodeWithTag("Submit Button").performClick()
  }
}
