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
import ch.epfl.skysync.screens.getStartOfWeek
import java.time.LocalDate
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
        homeGraph(navController, null)
      }
      navController.navigate(Route.AVAILABILITY_CALENDAR)
    }
  }

  @Test
  fun verifyFlightCalendarIsStartDestination() {
    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.AVAILABILITY_CALENDAR)
  }

  @Test
  fun routeIsRightIfClickOnAddTodayDate() {
    var previewDate = LocalDate.now()
    val testTag = previewDate.toString() + TimeSlot.AM.toString()
    composeTestRule.onNode(hasTestTag(testTag)).performClick()

    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.AVAILABILITY_CALENDAR)
  }

  @Test
  fun routeIsRightIfClickOnMondayAm() {
    var previewDate = getStartOfWeek(LocalDate.now())
    val testTag = previewDate.toString() + TimeSlot.AM.toString()
    composeTestRule.onNode(hasTestTag(testTag)).performClick()

    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.AVAILABILITY_CALENDAR)
  }

  @Test
  fun routeIsRightIfClickOnMondayPm() {
    var previewDate = getStartOfWeek(LocalDate.now())
    val testTag = previewDate.toString() + TimeSlot.PM.toString()
    composeTestRule.onNode(hasTestTag(testTag)).performClick()

    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.AVAILABILITY_CALENDAR)
  }

  @Test
  fun routeIsRightIfClickOnTuesdayAm() {
    var previewDate = getStartOfWeek(LocalDate.now()).plusDays(1)
    val testTag = previewDate.toString() + TimeSlot.AM.toString()
    composeTestRule.onNode(hasTestTag(testTag)).performClick()

    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.AVAILABILITY_CALENDAR)
  }

  @Test
  fun routeIsRightIfClickOnTuesdayPm() {
    var previewDate = getStartOfWeek(LocalDate.now()).plusDays(1)
    val testTag = previewDate.toString() + TimeSlot.PM.toString()
    composeTestRule.onNode(hasTestTag(testTag)).performClick()

    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.AVAILABILITY_CALENDAR)
  }

  @Test
  fun routeIsRightIfClickOnWednesdayAm() {
    var previewDate = getStartOfWeek(LocalDate.now()).plusDays(2)
    val testTag = previewDate.toString() + TimeSlot.AM.toString()
    composeTestRule.onNode(hasTestTag(testTag)).performClick()

    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.AVAILABILITY_CALENDAR)
  }

  @Test
  fun routeIsRightIfClickOnWednesdayPm() {
    var previewDate = getStartOfWeek(LocalDate.now()).plusDays(2)
    val testTag = previewDate.toString() + TimeSlot.PM.toString()
    composeTestRule.onNode(hasTestTag(testTag)).performClick()

    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.AVAILABILITY_CALENDAR)
  }

  @Test
  fun routeIsRightIfClickOnNext() {
    composeTestRule.onNodeWithText("Next Week").performClick()
    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.AVAILABILITY_CALENDAR)
  }

  @Test
  fun routeIsRightIfClickOnPrev() {
    composeTestRule.onNodeWithText("Prev Week").performClick()

    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.AVAILABILITY_CALENDAR)
  }
}
