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

class PilotReportScreenTest {
  @get:Rule val composeTestRule = createComposeRule()
    private lateinit var navController : TestNavHostController
    private val db = FirestoreDatabase(useEmulator = true)
    private val dbs = DatabaseSetup()
    private val repository = Repository(db)


    @Before
  fun setUp() {
    composeTestRule.setContent {
        navController = TestNavHostController(LocalContext.current)
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        NavHost(navController = navController, startDestination = Route.MAIN) {
            homeGraph(repository, navController, dbs.pilot1.id)
        }
        navController.navigate(Route.PILOT_REPORT+"/testID")
    }
  }

  @Test
  fun allFieldsAreDisplayed() {
    composeTestRule
        .onNodeWithTag("Pilot Report LazyColumn")
        .performScrollToNode(hasTestTag("Number of passengers"))
        .assertExists()
    composeTestRule
        .onNodeWithTag("Pilot Report LazyColumn")
        .performScrollToNode(hasTestTag("Takeoff time"))
        .assertExists()
    composeTestRule
        .onNodeWithTag("Pilot Report LazyColumn")
        .performScrollToNode(hasTestTag("Takeoff location Search Bar Input"))
        .assertExists()
    composeTestRule
        .onNodeWithTag("Pilot Report LazyColumn")
        .performScrollToNode(hasTestTag("Landing time"))
        .assertExists()
    composeTestRule
        .onNodeWithTag("Pilot Report LazyColumn")
        .performScrollToNode(hasTestTag("Landing location Search Bar Input"))
        .assertExists()
    composeTestRule
        .onNodeWithTag("Pilot Report LazyColumn")
        .performScrollToNode(hasTestTag("Effective time of start"))
        .assertExists()
    composeTestRule
        .onNodeWithTag("Pilot Report LazyColumn")
        .performScrollToNode(hasTestTag("Effective time of end"))
        .assertExists()
    composeTestRule
        .onNodeWithTag("Pilot Report LazyColumn")
        .performScrollToNode(hasTestTag("Pause duration"))
        .assertExists()
    composeTestRule
        .onNodeWithTag("Pilot Report LazyColumn")
        .performScrollToNode(hasTestTag("Comments"))
        .assertExists()
    composeTestRule.onNodeWithTag("Submit Button").assertExists()
  }

    @Test
    fun testSubmitMinimalReport(){
        composeTestRule
            .onNodeWithTag("Pilot Report LazyColumn")
            .performScrollToNode(hasTestTag("Number of passengers"))
        composeTestRule
            .onNodeWithTag("Number of passengers")
            .performTextClearance()
        composeTestRule
            .onNodeWithTag("Number of passengers")
            .performTextInput("5")

        composeTestRule
            .onNodeWithTag("Pilot Report LazyColumn")
            .performScrollToNode(hasTestTag("Effective time of start"))
        composeTestRule.onNodeWithTag("Effective time of start").performClick()

        inputTimePicker(composeTestRule,9, 10)

        composeTestRule
            .onNodeWithTag("Pilot Report LazyColumn")
            .performScrollToNode(hasTestTag("Effective time of end"))
        composeTestRule
            .onNodeWithTag("Effective time of end")
            .performClick()
        inputTimePicker(composeTestRule,11, 15)

        composeTestRule.onNodeWithTag("Submit Button").assertExists()
    }
}
