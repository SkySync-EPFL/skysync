package ch.epfl.skysync.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeNavigationTest {

  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController

  @Before
  fun setUpNavHost() {
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      NavHost(navController = navController, startDestination = Route.MAIN) {
        //homeGraph(navController, null)
      }
    }
  }

  @Test
  fun verifyHomeIsStartDestination() {
    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.HOME)
  }

  @Test
  fun routeIsRightIfClickOnCalendar() {
    composeTestRule.onNodeWithText("Calendar").performClick()

    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.AVAILABILITY_CALENDAR)
  }

  @Test
  fun routeIsRightIfClickOnFlight() {
    composeTestRule.onNodeWithText("Flight").performClick()

    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.FLIGHT)
  }

  @Test
  fun routeIsRightIfClickOnChat() {
    composeTestRule.onNodeWithText("Chat").performClick()

    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.CHAT)
  }
}
