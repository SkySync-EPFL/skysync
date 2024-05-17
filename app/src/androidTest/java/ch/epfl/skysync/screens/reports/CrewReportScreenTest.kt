package ch.epfl.skysync.screens.reports

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.navigation.testing.TestNavHostController
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
  private var navController: TestNavHostController = mockk(relaxed = true)
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  private val repository = Repository(db)
  private val viewModel = FinishedFlightsViewModel(repository, dbs.crew1.id)

  @Before
  fun setUp() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
    composeTestRule.setContent {
      viewModel.refresh()
      CrewReportScreen(
          navHostController = navController,
          finishedFlightsViewModel = viewModel,
          flightId = dbs.flight5.id)
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun allFieldsAreDisplayed() {
    composeTestRule.waitUntilExactlyOneExists(hasTestTag("Crew Report LazyColumn"), 2000)
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

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun minimalReportWorks() {
    composeTestRule.waitUntilExactlyOneExists(hasTestTag("Crew Report LazyColumn"))
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
        .performScrollToNode(hasTestTag("Comments"))
    composeTestRule.onNodeWithTag("Add Problem Button").performClick()
    composeTestRule.onNodeWithTag("Vehicle Menu").performClick()
    composeTestRule.onNodeWithTag("Vehicle 0").performClick()
    composeTestRule.onNodeWithTag("Problem Field").performTextInput("Problem 1")
    composeTestRule.onNodeWithTag("Add Vehicle Problem Button").performClick()

    composeTestRule.onNodeWithTag("Submit Button").performClick()
  }
}
