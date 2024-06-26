package ch.epfl.skysync.test_end_to_end

import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
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
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.navigation.homeGraph
import ch.epfl.skysync.viewmodel.InFlightViewModel
import ch.epfl.skysync.viewmodel.MessageListenerViewModel
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class E2EAddFlights {
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
    composeTestRule.waitUntil {
      val nodes = composeTestRule.onAllNodesWithText("Upcoming flights")
      nodes.fetchSemanticsNodes().isNotEmpty()
    }
  }

  @Test
  //    This test function simulates the process of adding a flight as an admin in a UI environment.
  //    It navigates through various UI elements, sets values, and confirms the addition of a
  // flight.
  //    Finally, it verifies if the flight with 13 passengers is successfully created.
  fun addFlightAsAdmin() {
    // Clicks on the "addFlightButton" to initiate adding a flight
    composeTestRule.onNodeWithTag("addFlightButton").performClick()

    // Checks the current destination route in the navigation controller
    var route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(Route.ADD_FLIGHT, route)

    // Performs scrolling to the "Number of passengers" field and sets its value to 13
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Number of passengers"))
    composeTestRule.onNodeWithTag("Number of passengers").performClick()
    composeTestRule.onNodeWithTag("Number of passengers").performTextClearance()
    composeTestRule.onNodeWithTag("Number of passengers").performTextInput("13")

    // Clicks on the "Date Field" and selects "OK" from the dialog
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Date Field"))
    composeTestRule.onNodeWithTag("Date Field").performClick()
    composeTestRule.onNodeWithText("OK").performClick()

    // Performs similar actions for selecting flight type, vehicle, time slot, balloon, and basket
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Flight Type Menu"))
    composeTestRule.onNodeWithTag("Flight Type Menu").performClick()
    composeTestRule.onNodeWithTag("Flight Type 1").performClick()

    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Vehicle 0 Menu"))
    composeTestRule.onNodeWithTag("Vehicle 0 Menu").performClick()
    composeTestRule.onNodeWithTag("Vehicle 0 1").performClick()

    val timeSlotTag = "Time Slot ${if(dbs.date2TimeSlot == TimeSlot.AM) 0 else 1}"
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Time Slot Menu"))
    composeTestRule.onNodeWithTag("Time Slot Menu").performClick()
    composeTestRule.onNodeWithTag(timeSlotTag).performClick()

    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Balloon Menu"))
    composeTestRule.onNodeWithTag("Balloon Menu").performClick()
    composeTestRule.onNodeWithTag("Balloon 0").performClick()

    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Basket Menu"))
    composeTestRule.onNodeWithTag("Basket Menu").performClick()
    composeTestRule.onNodeWithTag("Basket 0").performClick()

    // Clicks on the "Add Flight" button to confirm flight addition
    val title1 = "Add Flight"
    composeTestRule.onNodeWithTag("$title1 Button").performClick()

    // Checks if navigation goes back to the home route after adding flight
    route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(Route.ADMIN_HOME, route)

    // Checks if the flight with 13 passengers was created successfully
    var flightIsCreated = false
    runTest {
      val flights = repository.flightTable.getAll(onError = { assertNull(it) })
      flightIsCreated = flights.any { it.nPassengers == 13 }
    }
    Assert.assertEquals(true, flightIsCreated)
  }
}
