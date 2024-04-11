package ch.epfl.skysync

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

class CalendarUITest {
    @get:Rule val composeTestRule = createComposeRule()
    lateinit var navController: TestNavHostController

    @Before
    fun setUpNavHost() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = Route.MAIN) {
                homeGraph(navController)
            }
            navController.navigate(Route.FLIGHTCALENDAR)
        }
    }

    @Test
    fun verifyFlightCalendarIsStartDestination() {
        val route = navController.currentBackStackEntry?.destination?.route
        Assert.assertEquals(route, Route.FLIGHTCALENDAR)
    }

    @Test
    fun routeIsRightIfClickOnAdd() {
        composeTestRule.onNodeWithText("Add").performClick()

        val route = navController.currentBackStackEntry?.destination?.route
        Assert.assertEquals(route, Route.ADDFLIGHT)
    }

    @Test
    fun routeIsRightIfClickOnNextAndAdd() {
        composeTestRule.onNodeWithText("Next Week").performClick()
        composeTestRule.onNodeWithText("Add").performClick()

        val route = navController.currentBackStackEntry?.destination?.route
        Assert.assertEquals(route, Route.ADDFLIGHT)
    }

    @Test
    fun routeIsRightIfClickOnPrevAndAdd() {
        composeTestRule.onNodeWithText("Prev Week").performClick()
        composeTestRule.onNodeWithText("Add").performClick()

        val route = navController.currentBackStackEntry?.destination?.route
        Assert.assertEquals(route, Route.ADDFLIGHT)
    }
}