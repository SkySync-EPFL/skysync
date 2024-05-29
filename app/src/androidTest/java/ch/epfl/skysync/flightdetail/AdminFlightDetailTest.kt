package ch.epfl.skysync.flightdetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.AdminConfirmedFlightDetailBottom
import ch.epfl.skysync.components.ContextConnectivityStatus
import ch.epfl.skysync.components.FinishedFlightDetailBottom
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.screens.admin.AdminFlightDetailScreen
import ch.epfl.skysync.viewmodel.FlightsViewModel
import ch.epfl.skysync.viewmodel.InFlightViewModel
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AdminFlightDetailTest {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var navController: NavHostController
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  private val repository = Repository(db)

  @Before
  fun setUpNavHost() =
      runTest() {
        dbs.clearDatabase(db)
        dbs.fillDatabase(db)
      }

  @Test
  fun accessDetailsOfConfirmedFlight() = runTest {
    lateinit var viewModel: FlightsViewModel
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      val inFlightViewModel = InFlightViewModel.createViewModel(repository)
      viewModel = FlightsViewModel.createViewModel(repository, dbs.admin1.id)

      val context = LocalContext.current
      val connectivityStatus = remember { ContextConnectivityStatus(context) }
      AdminFlightDetailScreen(
          navController, dbs.flight4.id, viewModel, inFlightViewModel, connectivityStatus)
    }
    viewModel.refreshUserAndFlights().join()
    composeTestRule.waitUntil {
      composeTestRule.onAllNodesWithText("Flight status").fetchSemanticsNodes().isNotEmpty()
    }
  }

  @Test
  fun accessDetailsOfFinishedFlight() = runTest {
    lateinit var viewModel: FlightsViewModel
    composeTestRule.setContent {
      val context = LocalContext.current
      val connectivityStatus = remember { ContextConnectivityStatus(context) }
      val inFlightViewModel = InFlightViewModel.createViewModel(repository)
      viewModel = FlightsViewModel.createViewModel(repository, dbs.admin1.id)
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      AdminFlightDetailScreen(
          navController, dbs.finishedFlight1.id, viewModel, inFlightViewModel, connectivityStatus)
    }
    viewModel.refreshUserAndFlights().join()
    composeTestRule.waitUntil {
      composeTestRule.onAllNodesWithText("Flight status").fetchSemanticsNodes().isNotEmpty()
    }
  }

  @Test
  fun testAdminConfirmBottomHasClickableButtons() = runTest {
    var okWasClicked = false
    var deleteWasClicked = false
    composeTestRule.setContent {
      AdminConfirmedFlightDetailBottom(
          okClick = { okWasClicked = true }, deleteClick = { deleteWasClicked = true })
    }
    composeTestRule.onNodeWithTag("Ok").performClick()
    composeTestRule.onNodeWithTag("Delete").performClick()
    assertTrue(okWasClicked)
    assertTrue(deleteWasClicked)
  }

  @Test
  fun testAdminFinishedFlightBottomHasClickableButtons() = runTest {
    var reportWasClicked = false
    var traceWasClicked = false
    composeTestRule.setContent {
      FinishedFlightDetailBottom(
          reportClick = { reportWasClicked = true },
          flightTraceClick = { traceWasClicked = true },
      )
    }
    composeTestRule.onNodeWithTag("Report").performClick()
    composeTestRule.onNodeWithTag("Flight Trace").performClick()
    assertTrue(reportWasClicked)
    assertTrue(traceWasClicked)
  }
}
