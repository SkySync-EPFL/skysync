package ch.epfl.skysync.test_end_to_end

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
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
import ch.epfl.skysync.database.DateUtility
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.FlightStatus
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.navigation.homeGraph
import ch.epfl.skysync.viewmodel.InFlightViewModel
import ch.epfl.skysync.viewmodel.MessageListenerViewModel
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
  }

  private fun helper(field: String, value: String) {
    composeTestRule.onNodeWithText(field).assertIsDisplayed()
    composeTestRule.onNodeWithText(value).assertIsDisplayed()
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
      composeTestRule.onNodeWithTag("flightCard${flight.id}").performClick()
      var route = navController.currentBackStackEntry?.destination?.route
      assertEquals(Route.CREW_FLIGHT_DETAILS + "/{Flight ID}", route)

      composeTestRule.waitUntil(2500) {
        composeTestRule.onAllNodesWithText("Balloon").fetchSemanticsNodes().isNotEmpty()
      }
      var flightStatus = FlightStatus.PLANNED
      when (flight) {
        is ConfirmedFlight -> {
          flightStatus = FlightStatus.CONFIRMED
        }
        is FinishedFlight -> {
          flightStatus = FlightStatus.FINISHED
        }
      }
      helper("Flight status", flightStatus.toString())
      helper("Day of flight", DateUtility.localDateToString(flight.date))
      helper("Time slot", DateUtility.localDateToString(flight.date))
      helper("Number of Passengers", flight.nPassengers.toString())
      helper("Flight type", flight.flightType.name)
      helper("Balloon", flight.balloon?.name ?: "None")
      helper("Basket", flight.basket?.name ?: "None")

      composeTestRule.onNodeWithTag("FlightDetailLazyColumn").performScrollToNode(hasText("Team"))
      flight.vehicles.forEach {
        composeTestRule
            .onNodeWithTag("FlightDetailLazyColumn")
            .performScrollToNode(hasText(it.name))
        composeTestRule.onNodeWithText(it.name).assertIsDisplayed()
      }

      if (flight is ConfirmedFlight) {

        flight.team.roles.forEach { role ->
          composeTestRule
              .onNodeWithTag("FlightDetailLazyColumn")
              .performScrollToNode(hasText("Team"))
          composeTestRule.onNodeWithText("COLOR ${flight.color}").assertIsDisplayed()
          val metric = role.roleType.description
          val firstname = role.assignedUser?.firstname
          val lastname = role.assignedUser?.lastname
          composeTestRule.onNodeWithTag("Metric$metric$firstname").assertIsDisplayed()
          composeTestRule.onNodeWithTag("Metric$metric$lastname").assertIsDisplayed()
        }
        composeTestRule
            .onNodeWithTag("FlightDetailLazyColumn")
            .performScrollToNode(hasText("Meet up times"))
        composeTestRule.onNodeWithText("Team meet up time").assertIsDisplayed()
        composeTestRule
            .onNodeWithText(DateUtility.localTimeToString(flight.meetupTimeTeam))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Team departure time").assertIsDisplayed()
        composeTestRule
            .onNodeWithText(DateUtility.localTimeToString(flight.departureTimeTeam))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Passengers meet up time").assertIsDisplayed()
        composeTestRule
            .onNodeWithText(DateUtility.localTimeToString(flight.meetupTimePassenger))
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("FlightDetailLazyColumn")
            .performScrollToNode(hasText("Passengers meet up location"))
        composeTestRule.onNodeWithText(flight.meetupLocationPassenger).assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("FlightDetailLazyColumn")
            .performScrollToNode(hasText("Remarks"))
        flight.remarks.forEach { r -> composeTestRule.onNodeWithText(r).assertIsDisplayed() }
      }
      composeTestRule.onNodeWithText("OK").assertIsDisplayed()
      composeTestRule.onNodeWithTag("BackButton").performClick()
      route = navController.currentBackStackEntry?.destination?.route
      assertEquals(Route.CREW_HOME, route)
    }
  }
}
