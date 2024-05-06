package ch.epfl.skysync.test_end_to_end

import androidx.compose.ui.platform.LocalContext
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
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.navigation.homeGraph
import junit.framework.TestCase.assertEquals
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
      NavHost(navController = navController, startDestination = Route.MAIN) {
        homeGraph(repository, navController, dbs.admin1.id)
      }
    }
    composeTestRule.waitUntil(2500) {
      val nodes = composeTestRule.onAllNodesWithText("Upcoming flights")
      nodes.fetchSemanticsNodes().isNotEmpty()
    }
  }

  @Test
  fun modifyAndDeleteFlight() {
    runTest {
      val assignedFlight =
          listOf(dbs.flight1, dbs.flight2, dbs.flight3, dbs.flight4).sortedBy { flight: Flight ->
            flight.id
          }
      val retrievedFlights =
          repository.flightTable.getAll(onError = { Assert.assertNull(it) }).sortedBy {
              flight: Flight ->
            flight.id
          }

      assertEquals(assignedFlight, retrievedFlights)
      val plannedFlight = dbs.flight1
      composeTestRule.waitUntil(2500) {
        composeTestRule
            .onAllNodesWithTag("flightCard + ${plannedFlight.id}")
            .fetchSemanticsNodes()
            .isNotEmpty()
      }
      composeTestRule.onNodeWithTag("flightCard + ${plannedFlight.id}").performClick()
      var route = navController.currentBackStackEntry?.destination?.route
      Assert.assertEquals(Route.FLIGHT_DETAILS + "/{Flight ID}", route)
      composeTestRule.onNodeWithTag("EditButton").performClick()
      route = navController.currentBackStackEntry?.destination?.route
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

      // Clicks on the "Date Field" and selects "OK" from the dialog

      composeTestRule
          .onNodeWithTag("Flight Lazy Column")
          .performScrollToNode(hasTestTag("Date Field"))
      composeTestRule.onNodeWithTag("Date Field").performClick()
      composeTestRule.onNodeWithText("OK").performClick()

      // Performs similar actions for selecting flight type, vehicle, time slot, balloon, and basket
      // (fails because of coroutines)

      // Clicks on the "Add Flight" button to confirm flight addition
      val title1 = "Modify Flight"
      composeTestRule.onNodeWithTag("$title1 Button").performClick()

      // Checks if navigation goes back to the home route after adding flight
      route = navController.currentBackStackEntry?.destination?.route
      Assert.assertEquals(Route.HOME, route)
      var flightIsCreated = false
      val flights = repository.flightTable.getAll(onError = { Assert.assertNotNull(it) })
      flightIsCreated = flights.any { it.nPassengers == 11 }
      Assert.assertEquals(true, flightIsCreated)

      composeTestRule.waitUntil(2500) {
        composeTestRule
            .onAllNodesWithTag("flightCard + ${plannedFlight.id}")
            .fetchSemanticsNodes()
            .isNotEmpty()
      }
      composeTestRule.onNodeWithTag("flightCard + ${plannedFlight.id}").performClick()
      route = navController.currentBackStackEntry?.destination?.route
      Assert.assertEquals(Route.FLIGHT_DETAILS + "/{Flight ID}", route)
      composeTestRule.onNodeWithTag("DeleteButton").performClick()
      composeTestRule.onNodeWithTag("AlertDialogConfirm").performClick()
      composeTestRule.waitUntil(2500) {
        route = navController.currentBackStackEntry?.destination?.route
        Route.HOME == route
      }
    }
  }
}
