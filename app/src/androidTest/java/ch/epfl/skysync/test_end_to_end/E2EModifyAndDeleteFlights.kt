package ch.epfl.skysync.test_end_to_end

import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.ContextConnectivityStatus
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.navigation.homeGraph
import ch.epfl.skysync.viewmodel.InFlightViewModel
import ch.epfl.skysync.viewmodel.MessageListenerViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class E2EModifyAndDeleteFlights {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  private val repository = Repository(db)

  @Before
  fun setUpNavHost() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      val inFlightViewModel = InFlightViewModel.createViewModel(repository)
      val messageListenerViewModel = MessageListenerViewModel.createViewModel()
      val context = LocalContext.current
      val connectivityStatus = remember { ContextConnectivityStatus(context) }
      NavHost(navController = navController, startDestination = Route.MAIN) {
        homeGraph(
            repository,
            navController,
            dbs.admin1.id,
            inFlightViewModel,
            messageListenerViewModel,
            connectivityStatus)
      }
    }
    composeTestRule.waitUntil(2500) {
      val nodes = composeTestRule.onAllNodesWithText("Upcoming flights")
      nodes.fetchSemanticsNodes().isNotEmpty()
    }
  }

  private fun clickOnFlight(flight: Flight) {
    composeTestRule.onNodeWithTag("flightCard${flight.id}").performClick()
    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(Route.ADMIN_FLIGHT_DETAILS + "/{Flight ID}", route)
    composeTestRule.waitUntil(2500) {
      composeTestRule.onAllNodesWithText("Flight status").fetchSemanticsNodes().isNotEmpty()
    }
  }
  /**
   * scenario:
   * 1) click on flight1 in upcomming flights
   * 2) click on edit button
   * 3) modify flight => number of passengers, date, flight type, vehicle, time slot, balloon,
   *    basket
   */
  @Test
  fun modifyAndDeleteFlight() = runTest {
    val flight = dbs.flight6

    composeTestRule.waitUntil(2500) {
      composeTestRule.onAllNodesWithTag("flightCard${flight.id}").fetchSemanticsNodes().isNotEmpty()
    }
    clickOnFlight(flight = flight)

    composeTestRule.onNodeWithTag("delete").assertIsDisplayed()
    composeTestRule.onNodeWithTag("edit").assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirm").assertIsDisplayed()

    composeTestRule.onNodeWithTag("edit").performClick()

    var route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(Route.MODIFY_FLIGHT + "/{Flight ID}", route)
    composeTestRule.waitUntil(2500) {
      composeTestRule.onAllNodesWithTag("Flight Lazy Column").fetchSemanticsNodes().isNotEmpty()
    }
    composeTestRule.onNodeWithTag("Flight Lazy Column").assertExists()

    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Number of passengers"))
    composeTestRule.onNodeWithTag("Number of passengers").performClick()
    composeTestRule.onNodeWithTag("Number of passengers").performTextClearance()
    composeTestRule.onNodeWithTag("Number of passengers").performTextInput("11")

    val tag = if (dbs.date2TimeSlotInverse == TimeSlot.AM) "Time Slot 0" else "Time Slot 1"
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Time Slot Menu"))
    composeTestRule.onNodeWithTag("Time Slot Menu").performClick()
    composeTestRule.onNodeWithTag(tag).performClick()

    // Clicks on the "Date Field" and selects "OK" from the dialog

    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Date Field"))
    composeTestRule.onNodeWithTag("Date Field").performClick()
    composeTestRule.onNodeWithText("OK").performClick()

    // Performs similar actions for selecting flight type, vehicle, time slot, balloon, and basket
    // (fails because of coroutines)

    // Clicks on the "Add Flight" button to confirm flight modification
    val title1 = "Modify Flight"
    composeTestRule.onNodeWithTag("$title1 Button").performClick()

    composeTestRule.waitUntil(2500) {
      val nodes = composeTestRule.onAllNodesWithText("Upcoming flights")
      nodes.fetchSemanticsNodes().isNotEmpty()
    }

    composeTestRule.waitUntil(2500) {
      composeTestRule.onAllNodesWithTag("flightCard${flight.id}").fetchSemanticsNodes().isNotEmpty()
    }

    clickOnFlight(flight = flight)

    composeTestRule.onNodeWithTag("delete").performClick()
    composeTestRule.onNodeWithTag("AlertDialogConfirm").performClick()
    route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(Route.ADMIN_HOME, route)

    composeTestRule.waitUntil(2500) {
      composeTestRule
          .onAllNodesWithTag("flightCard${dbs.flight2.id}")
          .fetchSemanticsNodes()
          .isNotEmpty()
    }
    composeTestRule.onNodeWithTag("flightCard${flight.id}").assertIsNotDisplayed()
  }
}
