package ch.epfl.skysync.screens.home

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
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
import ch.epfl.skysync.screens.ModifyFlightScreen
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
  fun openModifyAFlight() {
    composeTestRule.setContent {
      viewModelAdmin = FlightsViewModel.createViewModel(repository, "id-admin-1")
      val navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      NavHost(navController = navController, startDestination = Route.MAIN) {
        homeGraph(repository, navController, dbSetup.admin1.id)
      }
      ModifyFlightScreen(
          navController = navController, viewModel = viewModelAdmin, flightId = dbSetup.flight1.id)
    }
    // composeTestRule.onNodeWithTag("Modify Flight Button").performClick()
  }
}