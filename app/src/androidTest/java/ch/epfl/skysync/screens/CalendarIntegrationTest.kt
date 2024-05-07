package ch.epfl.skysync.screens

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.navigation.homeGraph
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CalendarIntegrationTest {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()

  @Before
  fun setUpNavHost() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
    composeTestRule.setContent {
      val repository = Repository(db)
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      NavHost(navController = navController, startDestination = Route.MAIN) {
        homeGraph(repository, navController, dbs.pilot2.id)
      }
    }
  }

  @Test
  fun seeAndAccessPersonalFlight() {
    composeTestRule.waitUntil {
      val nodes = composeTestRule.onAllNodesWithText("Upcoming flights")
      nodes.fetchSemanticsNodes().isNotEmpty()
    }
    composeTestRule.onNodeWithText("Calendar").performClick()
    composeTestRule.onNodeWithTag(Route.FLIGHT_CALENDAR).performClick()
    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.FLIGHT_CALENDAR)

    composeTestRule.waitUntil(3000) {
      val nodes = composeTestRule.onAllNodesWithText(dbs.flight2.flightType.name)
      nodes.fetchSemanticsNodes().isNotEmpty()
    }
    composeTestRule.onNodeWithText(dbs.flight2.flightType.name).assertIsDisplayed()
  }
}
