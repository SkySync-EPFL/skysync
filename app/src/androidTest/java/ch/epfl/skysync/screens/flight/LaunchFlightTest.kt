package ch.epfl.skysync.screens.flight

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.isDisplayed
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
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.navigation.homeGraph
import ch.epfl.skysync.screens.crewpilot.LaunchFlight
import ch.epfl.skysync.viewmodel.FlightsViewModel
import ch.epfl.skysync.viewmodel.InFlightViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LaunchFlightTest {
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbSetup = DatabaseSetup()
  private val repository = Repository(db)
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var viewModel: FlightsViewModel
  lateinit var inFlightViewModel: InFlightViewModel
  lateinit var navController: TestNavHostController

  @Before
  fun setUp() = runTest {
    dbSetup.clearDatabase(db)
    dbSetup.fillDatabase(db)
  }

  @Test
  fun checkCrewNoFlightLaunched() {
    runTest {
      composeTestRule.setContent {
        inFlightViewModel = InFlightViewModel.createViewModel(repository)
        navController = TestNavHostController(LocalContext.current)
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        viewModel = FlightsViewModel.createViewModel(repository, dbSetup.crew1.id)
        LaunchFlight(navController, viewModel, inFlightViewModel)
      }
      inFlightViewModel.init(dbSetup.crew1.id).join()
      composeTestRule.onNodeWithText("No flight started").assertExists()
    }
  }

  @Test
  fun checkPilotNoFlightConfirmed() {
    runTest {
      composeTestRule.setContent {
        inFlightViewModel = InFlightViewModel.createViewModel(repository)
        navController = TestNavHostController(LocalContext.current)
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        viewModel = FlightsViewModel.createViewModel(repository, dbSetup.pilot3.id)
        LaunchFlight(navController, viewModel, inFlightViewModel)
      }
      inFlightViewModel.init(dbSetup.pilot3.id).join()
      composeTestRule
          .onNodeWithText(
              "No flight ready to be launched",
          )
          .assertExists()
    }
  }

  @Test
  fun checkPilotFlightConfirmed() {
    runTest {
      composeTestRule.setContent {
        inFlightViewModel = InFlightViewModel.createViewModel(repository)
        navController = TestNavHostController(LocalContext.current)
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        viewModel = FlightsViewModel.createViewModel(repository, dbSetup.pilot1.id)
        LaunchFlight(navController, viewModel, inFlightViewModel)
      }
      inFlightViewModel.init(dbSetup.pilot1.id).join()
      composeTestRule.onNodeWithTag("flightCard${dbSetup.flight4.id}").assertExists()
    }
  }

  @Test
  fun checkPilotFlightLaunched() {
    runTest {
      composeTestRule.setContent {
        inFlightViewModel = InFlightViewModel.createViewModel(repository)
        navController = TestNavHostController(LocalContext.current)
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        viewModel = FlightsViewModel.createViewModel(repository, dbSetup.pilot1.id)
        NavHost(navController = navController, startDestination = Route.MAIN) {
          homeGraph(repository, navController, dbSetup.pilot1.id, inFlightViewModel)
        }
      }
      composeTestRule.waitUntil {
        val nodes = composeTestRule.onAllNodesWithText("Upcoming flights")
        nodes.fetchSemanticsNodes().isNotEmpty()
      }
      composeTestRule.onNodeWithText("Flight").performClick()
      inFlightViewModel.init(dbSetup.pilot1.id).join()
      inFlightViewModel.setCurrentFlight(dbSetup.flight4.id)
      composeTestRule.waitUntil(3000) { composeTestRule.onNodeWithTag("Timer").isDisplayed() }
      val route = navController.currentBackStackEntry?.destination?.route
      Assert.assertEquals(route, Route.FLIGHT)
    }
  }

  @Test
  fun checkCrewFlightLaunched() {
    runTest {
      composeTestRule.setContent {
        inFlightViewModel = InFlightViewModel.createViewModel(repository)
        navController = TestNavHostController(LocalContext.current)
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        viewModel = FlightsViewModel.createViewModel(repository, dbSetup.crew1.id)
        NavHost(navController = navController, startDestination = Route.MAIN) {
          homeGraph(repository, navController, dbSetup.pilot1.id, inFlightViewModel)
        }
      }
      composeTestRule.waitUntil {
        val nodes = composeTestRule.onAllNodesWithText("Upcoming flights")
        nodes.fetchSemanticsNodes().isNotEmpty()
      }
      composeTestRule.onNodeWithText("Flight").performClick()
      inFlightViewModel.init(dbSetup.crew1.id).join()
      inFlightViewModel.setCurrentFlight(dbSetup.flight4.id)
      composeTestRule.waitUntil(3000) { composeTestRule.onNodeWithTag("Timer").isDisplayed() }
      val route = navController.currentBackStackEntry?.destination?.route
      Assert.assertEquals(route, Route.FLIGHT)
    }
  }
}
