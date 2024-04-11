package ch.epfl.skysync

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.navigation.homeGraph
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

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
            navController.navigate(Route.CALENDAR)
        }
    }

    @Test
    fun verifyFlightCalendarIsStartDestination() {
        val route = navController.currentBackStackEntry?.destination?.route
        Assert.assertEquals(route, Route.CALENDAR)
    }

    @Test
    fun routeIsRightIfClickOnAdd() {
        var previewDate = LocalDate.now()
        val testTag = previewDate.toString()+ TimeSlot.AM.toString()
        composeTestRule.onNode(hasTestTag(testTag)).performClick()

        val route = navController.currentBackStackEntry?.destination?.route
        Assert.assertEquals(route, Route.CALENDAR)
    }

    @Test
    fun routeIsRightIfClickOnNextAndAdd() {
        composeTestRule.onNodeWithText("Next Week").performClick()

        val route = navController.currentBackStackEntry?.destination?.route
        Assert.assertEquals(route, Route.CALENDAR)
    }

    @Test
    fun routeIsRightIfClickOnPrevAndAdd() {
        composeTestRule.onNodeWithText("Prev Week").performClick()

        val route = navController.currentBackStackEntry?.destination?.route
        Assert.assertEquals(route, Route.CALENDAR)
    }
}