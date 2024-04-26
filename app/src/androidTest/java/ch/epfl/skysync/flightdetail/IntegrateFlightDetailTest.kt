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
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.navigation.homeGraph
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class IntegrateFlightDetailTest {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController

  @Before
  fun setUpNavHost() {
    runTest {
      val db = FirestoreDatabase(useEmulator = true)
      val repository = Repository(db)
      val dbs = DatabaseSetup()
      dbs.clearDatabase(db)
      dbs.fillDatabase(db)
      composeTestRule.setContent {
        navController = TestNavHostController(LocalContext.current)
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        NavHost(navController = navController, startDestination = Route.MAIN) {
          homeGraph(repository, navController, null)
        }
      }
    }
  }

  // @Test
  //  fun modifyConfirm() {
  //    composeTestRule.onNodeWithText("Home").performClick()
  //    val nodes = composeTestRule.onAllNodesWithTag("flightCard")
  //    nodes[0].performClick()
  //    var route = navController.currentBackStackEntry?.destination?.route
  //    assertEquals(route, Route.FLIGHT_DETAILS + "/{Flight ID}")
  //    composeTestRule.onNodeWithTag("EditButton").performClick()
  //    route = navController.currentBackStackEntry?.destination?.route
  //    assertEquals(route, Route.MODIFY_FLIGHT + "/{Flight ID}")
  //    composeTestRule.onNodeWithTag("BackButton").performClick()
  //    route = navController.currentBackStackEntry?.destination?.route
  //    assertEquals(route, Route.FLIGHT_DETAILS + "/{Flight ID}")
  //    composeTestRule.onNodeWithTag("ConfirmButton").performClick()
  //    route = navController.currentBackStackEntry?.destination?.route
  //    assertEquals(route, Route.CONFIRM_FLIGHT + "/{Flight ID}")
  //  }

  //  @Test
  //  fun testDelete() {
  //    composeTestRule.onNodeWithText("Home").performClick()
  //    val nodes = composeTestRule.onAllNodesWithTag("flightCard")
  //    nodes[0].performClick()
  //    var route = navController.currentBackStackEntry?.destination?.route
  //    assertEquals(route, Route.FLIGHT_DETAILS + "/{Flight ID}")
  //    composeTestRule.onNodeWithTag("DeleteButton").performClick()
  //    route = navController.currentBackStackEntry?.destination?.route
  //    assertEquals(route, Route.HOME)
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
