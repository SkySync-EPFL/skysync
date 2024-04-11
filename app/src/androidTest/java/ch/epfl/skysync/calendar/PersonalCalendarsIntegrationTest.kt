package ch.epfl.skysync.calendar

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.navigation.homeGraph
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PersonalCalendarsIntegrationTest {

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
  fun switchBetweenCalendars() {
    composeTestRule.onNodeWithText("Calendar").performClick()
    var route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.AVAILABILITY_CALENDAR)

    composeTestRule.onNodeWithText("Flight Calendar").performClick()
    route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.PERSONAL_FLIGHT_CALENDAR)

    composeTestRule.onNodeWithText("Availability Calendar").performClick()
    route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.PERSONAL_FLIGHT_CALENDAR)

    composeTestRule.onNodeWithText("Flight").performClick()
    route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.FLIGHT)

    composeTestRule.onNodeWithText("Calendar").performClick()
    route = navController.currentBackStackEntry?.destination?.route
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
