package ch.epfl.skysync.screens.home

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.screens.admin.ModifyFlightScreen
import ch.epfl.skysync.viewmodel.FlightsViewModel
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ModifyFlightTest {

  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  @get:Rule val mockkRule = MockKRule(this)
  @RelaxedMockK lateinit var navController: NavHostController
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
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)

    composeTestRule.setContent {
      viewModelAdmin = FlightsViewModel.createViewModel(repository, dbs.admin1.id)
      ModifyFlightScreen(navController = navController, viewModel = viewModelAdmin)
    }
  }

  @Test
  fun checkModifyPassengerCountWorks() = runTest {
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
          it.nPassengers == 24 && it.id == dbs.flight1.id
        },
        true)
  }

  @Test
  fun checkAssignVehicleIsPersistent() = runTest {
    viewModelAdmin.refreshUserAndFlights().join()
    assertEquals(
        true,
        viewModelAdmin.currentFlights.value?.any {
          it.vehicles.contains(dbs.vehicle1) && it.id == dbs.flight1.id && it.vehicles.size == 1
        })
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("RoleField 0"))
    composeTestRule.onNodeWithTag("Delete Crew Member 0").performClick()
    viewModelAdmin.refreshUserAndFlights().join()
    assertTrue(
        viewModelAdmin.currentFlights.value?.any {
          it.vehicles.size == 1 && it.id == dbs.flight1.id
        } ?: false)
  }
}
