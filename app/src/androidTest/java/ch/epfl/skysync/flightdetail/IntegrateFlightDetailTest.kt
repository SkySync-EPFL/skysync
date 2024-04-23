package ch.epfl.skysync.flightdetail

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.navigation.homeGraph
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class IntegrateFlightDetailTest {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController

  @Before
  fun setUpNavHost() {
    composeTestRule.setContent {
      val repository = Repository(FirestoreDatabase(useEmulator = true))
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      NavHost(navController = navController, startDestination = Route.MAIN) {
        homeGraph(repository, navController, null)
      }
    }
  }

  //  @Test
  //  fun backStackIsRightIfClickOnFlight() {
  //    composeTestRule.onNodeWithText("Calendar").performClick()
  //    composeTestRule.onNodeWithText("Flight Calendar").performClick()
  //    val nodes = composeTestRule.onAllNodesWithTag("flightButton")
  //    for (i in 0 until nodes.fetchSemanticsNodes().size) {
  //      nodes[i].performClick()
  //      var route = navController.currentBackStackEntry?.destination?.route
  //      assertEquals(route, Route.FLIGHT_DETAILS + "/{Flight ID}")
  //      composeTestRule.onNodeWithText("Back").performClick()
  //      route = navController.currentBackStackEntry?.destination?.route
  //      assertEquals(route, Route.PERSONAL_FLIGHT_CALENDAR)
  //    }
  //  }

  @Test
  fun backStackIsRightIfClickOnFlightDetails() {
    composeTestRule.onNodeWithText("Home").performClick()
    val nodes = composeTestRule.onAllNodesWithTag("flightCard")
    for (i in 0 until nodes.fetchSemanticsNodes().size) {
      nodes[i].performClick()
      var route = navController.currentBackStackEntry?.destination?.route
      assertEquals(route, Route.FLIGHT_DETAILS + "/{Flight ID}")
      composeTestRule.onNodeWithText("Back").performClick()
      route = navController.currentBackStackEntry?.destination?.route
      assertEquals(route, Route.HOME)
    }
  }
}
