package ch.epfl.skysync.screens.home

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.tables.BalloonTable
import ch.epfl.skysync.database.tables.BasketTable
import ch.epfl.skysync.database.tables.FlightTable
import ch.epfl.skysync.database.tables.FlightTypeTable
import ch.epfl.skysync.database.tables.VehicleTable
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.navigation.homeGraph
import ch.epfl.skysync.screens.admin.ModifyFlightScreen
import ch.epfl.skysync.viewmodel.FlightsViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ModifyFlightTest {

  private val db = FirestoreDatabase(useEmulator = true)
  private val dbSetup = DatabaseSetup()
  private val flightTable = FlightTable(db)
  private val basketTable = BasketTable(db)
  private val balloonTable = BalloonTable(db)
  private val flightTypeTable = FlightTypeTable(db)
  private val vehicleTable = VehicleTable(db)
  private val repository = Repository(db)

  // adding this rule should set the test dispatcher and should
  // enable us to use advanceUntilIdle(), but it seems advanceUntilIdle
  // cancel the coroutine instead of waiting for it to finish
  // instead use the .join() for the moment
  // @ExperimentalCoroutinesApi @get:Rule var mainCoroutineRule = MainCoroutineRule()

  @get:Rule val composeTestRule = createComposeRule()
  lateinit var viewModelAdmin: FlightsViewModel

  @Before
  fun setUp() = runTest {
    dbSetup.clearDatabase(db)
    dbSetup.fillDatabase(db)
  }

  @Test
  fun checkModifyPassengerCountWorks() = runTest {
    composeTestRule.setContent {
      viewModelAdmin = FlightsViewModel.createViewModel(repository, "id-admin-1")
      val currentFlights = viewModelAdmin.currentFlights.collectAsStateWithLifecycle()
      val navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      NavHost(navController = navController, startDestination = Route.MAIN) {
        homeGraph(repository, navController, dbSetup.admin1.id)
      }
      ModifyFlightScreen(
          navController = navController, viewModel = viewModelAdmin, flightId = dbSetup.flight1.id)
    }
    viewModelAdmin.refreshUserAndFlights().join()
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Number of passengers"))
    composeTestRule.onNodeWithTag("Number of passengers").performClick()
    composeTestRule.onNodeWithTag("Number of passengers").performTextClearance()
    composeTestRule.onNodeWithTag("Number of passengers").performTextInput("24")
    composeTestRule.onNodeWithTag("Modify Flight Button").performClick()
    viewModelAdmin.refreshUserAndFlights().join()
    assertEquals(
        viewModelAdmin.currentFlights.value?.any {
          it.nPassengers == 24 && it.id == dbSetup.flight1.id
        },
        true)
  }

  @Test
  fun checkAssignVehicleIsPersistent() = runTest {
    composeTestRule.setContent {
      viewModelAdmin = FlightsViewModel.createViewModel(repository, "id-admin-1")
      val currentFlights = viewModelAdmin.currentFlights.collectAsStateWithLifecycle()
      val navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      NavHost(navController = navController, startDestination = Route.MAIN) {
        homeGraph(repository, navController, dbSetup.admin1.id)
      }
      ModifyFlightScreen(
          navController = navController, viewModel = viewModelAdmin, flightId = dbSetup.flight1.id)
    }
    viewModelAdmin.refreshUserAndFlights().join()
    assertEquals(
        true,
        viewModelAdmin.currentFlights.value?.any {
          it.vehicles.contains(dbSetup.vehicle1) &&
              it.id == dbSetup.flight1.id &&
              it.vehicles.size == 1
        })
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Vehicle 0 Menu"))
    composeTestRule.onNodeWithTag("Vehicle 0 Menu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Vehicle 0 Menu").performClick()
    composeTestRule.onNodeWithText("vehicle-2").performClick()
    composeTestRule.onNodeWithTag("Modify Flight Button").performClick()
    viewModelAdmin.refreshUserAndFlights().join()
    assertEquals(
        true,
        viewModelAdmin.currentFlights.value?.any {
          it.vehicles.contains(dbSetup.vehicle2) &&
              it.id == dbSetup.flight1.id &&
              !it.vehicles.contains(dbSetup.vehicle1) &&
              it.vehicles.size == 1
        })
  }
}
