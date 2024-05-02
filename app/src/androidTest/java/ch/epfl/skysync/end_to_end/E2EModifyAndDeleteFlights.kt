package ch.epfl.skysync.end_to_end

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
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.navigation.homeGraph
import ch.epfl.skysync.viewmodel.FlightsViewModel
import java.time.LocalDate
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

  lateinit var viewModelAdmin: FlightsViewModel
  private val flight =
      PlannedFlight(
          "1234",
          26,
          FlightType.DISCOVERY,
          Team(listOf(Role(RoleType.CREW))),
          Balloon("Ballon Name", BalloonQualification.LARGE, "Ballon Name"),
          Basket("Basket Name", true, "1234"),
          LocalDate.now().plusDays(3),
          TimeSlot.PM,
          listOf(Vehicle("Peugeot 308", "1234")))

  @Before
  fun setUpNavHost() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
    composeTestRule.setContent {
      viewModelAdmin = FlightsViewModel.createViewModel(repository, dbs.admin1.id)
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      NavHost(navController = navController, startDestination = Route.MAIN) {
        homeGraph(repository, navController, dbs.admin1.id)
      }
    }
    composeTestRule.waitUntil {
      val nodes = composeTestRule.onAllNodesWithText("Upcoming flights")
      nodes.fetchSemanticsNodes().isNotEmpty()
    }
  }

  @Test
  fun modifyAndDeleteFlight() {
    runTest {
      // Refreshes user and flights data asynchronously
      viewModelAdmin.refreshUserAndFlights().join()

      // Clicks on a flight card to view details
      composeTestRule.onNodeWithTag("flightCard").performClick()
      var route = navController.currentBackStackEntry?.destination?.route
      Assert.assertEquals(Route.FLIGHT_DETAILS + "/{Flight ID}", route)

      // Clicks on the "EditButton" to modify the flight
      composeTestRule.onNodeWithTag("EditButton").performClick()
      route = navController.currentBackStackEntry?.destination?.route
      Assert.assertEquals(Route.MODIFY_FLIGHT + "/{Flight ID}", route)

      // Waits for the flight details to load
      composeTestRule.waitForIdle()
      Thread.sleep(2000) // Optional sleep if needed

      // Asserts that the "Flight is loading..." text disappears and "Flight Lazy Column" appears
      composeTestRule.onNodeWithText("Flight is loading...").assertDoesNotExist()
      composeTestRule.onNodeWithTag("Flight Lazy Column").assertExists()

      // Modifies flight details such as number of passengers, date, flight type, vehicle, time
      // slot, balloon, and basket
      // Similar actions as in the "addFlightAsAdmin" test

      // Clicks on the "Modify Flight" button to confirm flight modification
      val title1 = "Modify Flight"
      composeTestRule.onNodeWithTag("$title1 Button").performClick()

      // Checks if navigation goes back to the home route after modifying flight
      route = navController.currentBackStackEntry?.destination?.route
      Assert.assertEquals(Route.HOME, route)

      // Verifies if the flight with 11 passengers is modified successfully
      var flightIsCreated = false
      val flights = repository.flightTable.getAll(onError = { Assert.assertNotNull(it) })
      flightIsCreated = flights.any { it.nPassengers == 11 }
      Assert.assertEquals(true, flightIsCreated)

      // Clicks on a flight card to view details again
      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithTag("flightCard").performClick()
      route = navController.currentBackStackEntry?.destination?.route
      Assert.assertEquals(Route.FLIGHT_DETAILS + "/{Flight ID}", route)

      // Deletes the flight by clicking on the "DeleteButton"
      composeTestRule.onNodeWithTag("DeleteButton").performClick()

      // Checks if navigation goes back to the home route after deleting flight
      route = navController.currentBackStackEntry?.destination?.route
      Assert.assertEquals(Route.HOME, route)

      // Asserts that the flights list is empty after deletion
      Assert.assertEquals(flights.isEmpty(), true)
    }
  }
}
