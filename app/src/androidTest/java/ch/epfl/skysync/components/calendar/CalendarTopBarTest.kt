package ch.epfl.skysync.screens.calendar

import androidx.compose.ui.platform.LocalContext
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

class CalendarTopBarTest {

  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController
  val dbs = DatabaseSetup()

  @Before
  fun setUpNavHost() = runTest {
    val db = FirestoreDatabase(useEmulator = true)
    val repository = Repository(db)
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      NavHost(navController = navController, startDestination = Route.MAIN) {
        homeGraph(repository, navController, dbs.crew1.id)
      }
    }
    composeTestRule.waitUntil {
      val nodes = composeTestRule.onAllNodesWithText("Upcoming flights")
      nodes.fetchSemanticsNodes().isNotEmpty()
    }
  }

  @Test
  fun switchBetweenCalendars() {
    composeTestRule.onNodeWithText("Calendar").performClick()

    var route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.AVAILABILITY_CALENDAR)

    composeTestRule.onNodeWithTag(Route.FLIGHT_CALENDAR).performClick()
    route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.FLIGHT_CALENDAR)

    composeTestRule.onNodeWithTag(Route.AVAILABILITY_CALENDAR).performClick()
    route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.AVAILABILITY_CALENDAR)
  }
}
