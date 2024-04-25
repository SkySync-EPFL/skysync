package ch.epfl.skysync

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.navigation.homeGraph
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ConfirmFlightScreenTest {
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

  @Test
  fun backStackIsRightIfClickOnFlightDetailsThenFlightConfirm() {
    composeTestRule.onNodeWithText("Home").performClick()
    val nodes = composeTestRule.onAllNodesWithTag("flightCard")
    for (i in 0 until nodes.fetchSemanticsNodes().size) {
      nodes[i].performClick()
      var route = navController.currentBackStackEntry?.destination?.route
      Assert.assertEquals(Route.FLIGHT_DETAILS + "/{Flight ID}", route)
      composeTestRule.onNodeWithText("Confirm").performClick()
      composeTestRule.waitForIdle()
      route = navController.currentBackStackEntry?.destination?.route
      Assert.assertEquals(Route.CONFIRM_FLIGHT + "/{Flight ID}", route)
      composeTestRule
          .onNodeWithTag("LazyList")
          .performScrollToNode(hasText("Confirm"))
          .assertIsDisplayed()
      composeTestRule.onNodeWithText("Confirm").performClick()
      composeTestRule.waitForIdle()
      route = navController.currentBackStackEntry?.destination?.route
      Assert.assertEquals(Route.HOME, route)
    }
  }
}
