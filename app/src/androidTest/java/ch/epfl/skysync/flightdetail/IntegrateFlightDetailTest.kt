package ch.epfl.skysync.flightdetail

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
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
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      NavHost(navController = navController, startDestination = Route.MAIN) {
        homeGraph(navController, null)
      }
    }
  }

  @Test
  fun backStackIsRightIfClickOnFlight() {
    composeTestRule.onNodeWithText("Calendar").performClick()
    composeTestRule.onNodeWithText("Flight Calendar").performClick()
    composeTestRule.onNodeWithText("Fondue").performClick()
    var route = navController.currentBackStackEntry?.destination?.route
    assertEquals(route, Route.FLIGHT_DETAILS + "/{Flight ID}")

    composeTestRule.onNodeWithText("Back").performClick()
    route = navController.currentBackStackEntry?.destination?.route
    assertEquals(route, Route.PERSONAL_FLIGHT_CALENDAR)

    composeTestRule.onNodeWithText("Discovery").performClick()
    route = navController.currentBackStackEntry?.destination?.route
    assertEquals(route, Route.FLIGHT_DETAILS + "/{Flight ID}")
  }

  @Test
  fun backStackIsRightIfClickOnFlightDetails() {
    composeTestRule.onNodeWithText("Home").performClick()
    composeTestRule.onNodeWithTag("1").performClick()
    var route = navController.currentBackStackEntry?.destination?.route
    assertEquals(route, Route.FLIGHT_DETAILS + "/{Flight ID}")

    composeTestRule.onNodeWithText("Back").performClick()
    route = navController.currentBackStackEntry?.destination?.route
    assertEquals(route, Route.HOME)

    composeTestRule.onNodeWithTag("2").performClick()
    route = navController.currentBackStackEntry?.destination?.route
    assertEquals(route, Route.FLIGHT_DETAILS + "/{Flight ID}")

    composeTestRule.onNodeWithText("Back").performClick()
    route = navController.currentBackStackEntry?.destination?.route
    assertEquals(route, Route.HOME)

    composeTestRule.onNodeWithTag("3").performClick()
    route = navController.currentBackStackEntry?.destination?.route
    assertEquals(route, Route.FLIGHT_DETAILS + "/{Flight ID}")

    composeTestRule.onNodeWithText("Back").performClick()
    route = navController.currentBackStackEntry?.destination?.route
    assertEquals(route, Route.HOME)
  }
}
