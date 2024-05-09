package ch.epfl.skysync.screens.reports

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.navigation.homeGraph
import ch.epfl.skysync.utils.inputTimePicker
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CrewReportScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var navController: TestNavHostController
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  private val repository = Repository(db)

  @Before
  fun setUp() {
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      NavHost(navController = navController, startDestination = Route.MAIN) {
        homeGraph(repository, navController, dbs.crew1.id)
      }
      navController.navigate(Route.CREW_REPORT)
    }
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

    composeTestRule.onNodeWithTag("Submit Button").performClick()
  }
}
