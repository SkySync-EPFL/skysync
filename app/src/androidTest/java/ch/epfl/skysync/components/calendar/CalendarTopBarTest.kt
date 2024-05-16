package ch.epfl.skysync.screens.calendar

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
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
import ch.epfl.skysync.viewmodel.InFlightViewModel
import ch.epfl.skysync.viewmodel.MessageListenerViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CalendarTopBarTest {

  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController
  val dbs = DatabaseSetup()
  val db = FirestoreDatabase(useEmulator = true)
  val repository = Repository(db)

  @Before
  fun setUpNavHost() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
  }

  @Test
  fun switchBetweenCalendars() {
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      val inFlightViewModel = InFlightViewModel.createViewModel(repository)
      val messageListenerViewModel = MessageListenerViewModel.createViewModel()

      NavHost(navController = navController, startDestination = Route.MAIN) {
        homeGraph(
            repository, navController, dbs.crew1.id, inFlightViewModel, messageListenerViewModel)
      }
    }
    composeTestRule.waitUntil {
      val nodes = composeTestRule.onAllNodesWithText("Upcoming flights")
      nodes.fetchSemanticsNodes().isNotEmpty()
    }
    composeTestRule.onNodeWithText("Calendar").performClick()

    var route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.CREW_AVAILABILITY_CALENDAR)

    composeTestRule.onNodeWithTag(Route.CREW_FLIGHT_CALENDAR).performClick()
    route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.CREW_FLIGHT_CALENDAR)

    composeTestRule.onNodeWithTag(Route.CREW_AVAILABILITY_CALENDAR).performClick()
    route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.CREW_AVAILABILITY_CALENDAR)
  }

  @Test
  fun adminSwitchBetweenCalendars() {
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      val inFlightViewModel = InFlightViewModel.createViewModel(repository)
      val messageListenerViewModel = MessageListenerViewModel.createViewModel()
      NavHost(navController = navController, startDestination = Route.MAIN) {
        homeGraph(
            repository, navController, dbs.admin1.id, inFlightViewModel, messageListenerViewModel)
      }
    }
    composeTestRule.waitUntil {
      val nodes = composeTestRule.onAllNodesWithText("Upcoming flights")
      nodes.fetchSemanticsNodes().isNotEmpty()
    }
    composeTestRule.onNodeWithText("Calendar").performClick()

    var route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.ADMIN_AVAILABILITY_CALENDAR)

    composeTestRule.onNodeWithTag(Route.ADMIN_FLIGHT_CALENDAR).performClick()
    route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.ADMIN_FLIGHT_CALENDAR)

    composeTestRule.onNodeWithTag(Route.ADMIN_AVAILABILITY_CALENDAR).performClick()
    route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.ADMIN_AVAILABILITY_CALENDAR)
  }
}
