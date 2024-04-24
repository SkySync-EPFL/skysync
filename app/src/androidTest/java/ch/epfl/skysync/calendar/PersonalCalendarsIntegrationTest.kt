package ch.epfl.skysync.calendar

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
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

class PersonalCalendarsIntegrationTest {

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
        homeGraph(repository, navController, dbs.admin1.id)
      }
    }
    composeTestRule.onNodeWithText("Calendar").performClick()
  }

  @Test
  fun startingRouteIsAvailabilityCalendar() {
    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(Route.AVAILABILITY_CALENDAR, route)
  }

  @Test
  fun saveButtonCorrectlyDisplayed() {
    composeTestRule.onNodeWithTag("AvailabilityCalendarSaveButton").performScrollTo()
    composeTestRule.onNodeWithTag("AvailabilityCalendarSaveButton").performScrollTo()
    composeTestRule.onNodeWithTag("AvailabilityCalendarSaveButton").assertIsDisplayed()
  }

  @Test
  fun saveButtonCorrectlyHasClickAction() {
    composeTestRule.onNodeWithTag("AvailabilityCalendarSaveButton").performScrollTo()
    composeTestRule.onNodeWithTag("AvailabilityCalendarSaveButton").performScrollTo()
    composeTestRule.onNodeWithTag("AvailabilityCalendarSaveButton").assertHasClickAction()
  }

  @Test
  fun switchBetweenCalendars() {
    composeTestRule.onNodeWithTag("ModularCalendar").assert(hasScrollAction())
    composeTestRule.onNodeWithTag("SwitchButtonLeftButton").performScrollTo()
    composeTestRule.onNodeWithTag("SwitchButtonLeftButton").performClick()

    var route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(Route.PERSONAL_FLIGHT_CALENDAR, route)

    composeTestRule.onNodeWithTag("SwitchButtonRightButton").performScrollTo()
    composeTestRule.onNodeWithTag("SwitchButtonRightButton").performClick()
    route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(Route.AVAILABILITY_CALENDAR, route)

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
