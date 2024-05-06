package ch.epfl.skysync.test_end_to_end

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.navigation.homeGraph
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * As staff I want to get more detailed information about a flight displayed in the overview when I
 * click on it, so that I can get more details for a specific flight
 */
@RunWith(AndroidJUnit4::class)
class E2ECrewFlightDetail {
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
        homeGraph(repository, navController, dbs.crew1.id)
      }
    }
    composeTestRule.waitUntil {
      val nodes = composeTestRule.onAllNodesWithText("Upcoming flights")
      nodes.fetchSemanticsNodes().isNotEmpty()
    }
  }

  @Test
  fun flightDetails() = runTest {
    composeTestRule.onNodeWithText("Upcoming flights").assertIsDisplayed()
    val assignedFlight =
        listOf(dbs.flight1, dbs.flight3, dbs.flight4).sortedBy { flight: Flight -> flight.id }
    val retrievedFlights =
        repository.userTable
            .retrieveAssignedFlights(
                repository.flightTable, dbs.crew1.id, onError = { Assert.assertNull(it) })
            .sortedBy { flight: Flight -> flight.id }

    assertEquals(assignedFlight, retrievedFlights)

    for (flight in assignedFlight) {
      composeTestRule.onNodeWithTag("flightCard + ${flight.id}").performClick()
      var route = navController.currentBackStackEntry?.destination?.route
      assertEquals(Route.FLIGHT_DETAILS + "/{Flight ID}", route)

      composeTestRule.waitUntil(2500) {
        composeTestRule.onAllNodesWithText("Balloon").fetchSemanticsNodes().isNotEmpty()
      }

      if (flight is PlannedFlight) {
        composeTestRule.onNodeWithText("Flight Detail").assertIsDisplayed()
        composeTestRule.onNodeWithText(flight.nPassengers.toString() + " Pax").assertIsDisplayed()

        composeTestRule.onNodeWithText("Balloon").assertIsDisplayed()
        var expected = flight.balloon?.name ?: "None"
        composeTestRule.onNodeWithTag("Balloon$expected").assertIsDisplayed()

        composeTestRule.onNodeWithText("Basket").assertIsDisplayed()
        expected = flight.basket?.name ?: "None"
        composeTestRule.onNodeWithTag("Basket$expected").assertIsDisplayed()

        composeTestRule.onNodeWithText(flight.flightType.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(flight.date.toString()).assertIsDisplayed()
      } else if (flight is ConfirmedFlight) {
        composeTestRule.onNodeWithText("Confirmed Flight").assertIsDisplayed()
        composeTestRule
            .onNodeWithTag("Number Of Pax" + flight.nPassengers.toString())
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(flight.flightType.name).assertIsDisplayed()

        flight.team.roles.forEachIndexed { i, _ ->
          composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Team $i"))
          composeTestRule.onNodeWithTag("Team $i").assertIsDisplayed()
        }

        composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Balloon"))
        composeTestRule.onNodeWithText("Balloon").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Balloon${flight.balloon.name}").assertIsDisplayed()

        composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Basket"))
        composeTestRule.onNodeWithTag("Basket").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Basket${flight.basket.name}").assertIsDisplayed()

        composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Date"))
        composeTestRule.onNodeWithText(flight.date.toString()).assertIsDisplayed()

        flight.vehicles.forEachIndexed { i, _ ->
          composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Vehicle $i"))
          composeTestRule.onNodeWithTag("Vehicle $i").assertIsDisplayed()
        }

        flight.remarks.forEachIndexed { i, _ ->
          composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Remark $i"))
          composeTestRule.onNodeWithTag("Remark $i").assertIsDisplayed()
        }

        composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Flight Color"))
        composeTestRule.onNodeWithTag("Flight Color").assertIsDisplayed()

        composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Meetup Time Team"))
        composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Departure Time Team"))
        composeTestRule
            .onNodeWithTag("body")
            .performScrollToNode(hasTestTag("Meetup Time Passenger"))
        composeTestRule
            .onNodeWithTag("body")
            .performScrollToNode(hasTestTag("Meetup Location Passenger"))
      }
      composeTestRule.onNodeWithText("Back").performClick()
      route = navController.currentBackStackEntry?.destination?.route
      assertEquals(Route.HOME, route)
    }
  }
}
