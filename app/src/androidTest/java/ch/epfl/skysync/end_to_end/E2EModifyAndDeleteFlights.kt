package ch.epfl.skysync.end_to_end

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertCountEquals
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
      viewModelAdmin.refreshUserAndFlights().join()
        composeTestRule.waitUntil(5000){
            composeTestRule.onAllNodesWithTag("flightCard").fetchSemanticsNodes().isNotEmpty()}
      composeTestRule.onAllNodesWithTag("flightCard")[0].performClick()
      var route = navController.currentBackStackEntry?.destination?.route
      Assert.assertEquals(Route.FLIGHT_DETAILS + "/{Flight ID}", route)
      composeTestRule.onNodeWithTag("EditButton").performClick()
      route = navController.currentBackStackEntry?.destination?.route
      Assert.assertEquals(Route.MODIFY_FLIGHT + "/{Flight ID}", route)
      viewModelAdmin.refreshUserAndFlights().join()
        composeTestRule.waitUntil(5000){
            composeTestRule.onAllNodesWithTag("Flight Lazy Column").fetchSemanticsNodes().isNotEmpty()}
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
      composeTestRule
          .onNodeWithTag("Flight Lazy Column")
          .performScrollToNode(hasTestTag("Flight Type Menu"))
      composeTestRule.onNodeWithTag("Flight Type Menu").performClick()
      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithTag("Flight Type 1").performClick()

      composeTestRule
          .onNodeWithTag("Flight Lazy Column")
          .performScrollToNode(hasTestTag("Vehicle 0 Menu"))
      composeTestRule.onNodeWithTag("Vehicle 0 Menu").performClick()
      composeTestRule.onNodeWithTag("Vehicle 0 1").performClick()

      composeTestRule
          .onNodeWithTag("Flight Lazy Column")
          .performScrollToNode(hasTestTag("Time Slot Menu"))
      composeTestRule.onNodeWithTag("Time Slot Menu").performClick()
      composeTestRule.onNodeWithTag("Time Slot 1").performClick()

      composeTestRule
          .onNodeWithTag("Flight Lazy Column")
          .performScrollToNode(hasTestTag("Balloon Menu"))
      composeTestRule.onNodeWithTag("Balloon Menu").performClick()
      composeTestRule.onNodeWithTag("Balloon 1").performClick()

      composeTestRule
          .onNodeWithTag("Flight Lazy Column")
          .performScrollToNode(hasTestTag("Basket Menu"))
      composeTestRule.onNodeWithTag("Basket Menu").performClick()
      composeTestRule.onNodeWithTag("Basket 1").performClick()

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

        composeTestRule.waitUntil(5000){
            composeTestRule.onAllNodesWithTag("flightCard").fetchSemanticsNodes().isNotEmpty()}
      composeTestRule.onNodeWithTag("flightCard").performClick()
      route = navController.currentBackStackEntry?.destination?.route
      Assert.assertEquals(Route.FLIGHT_DETAILS + "/{Flight ID}", route)
      composeTestRule.onNodeWithTag("DeleteButton").performClick()
        composeTestRule.onNodeWithText("Confirm").performClick()
        composeTestRule.waitUntil(5000){
            route = navController.currentBackStackEntry?.destination?.route
            Route.HOME== route}
      Assert.assertEquals(flights.isEmpty(), true)
    }
  }
}
