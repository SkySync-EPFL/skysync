package ch.epfl.skysync.screens.flight

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.screens.crewpilot.LaunchFlight
import ch.epfl.skysync.viewmodel.FlightsViewModel
import ch.epfl.skysync.viewmodel.InFlightViewModel
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LaunchFlightTest {
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbSetup = DatabaseSetup()
  private val repository = Repository(db)
  val confirmedFlight =
      ConfirmedFlight(
          id = "confirmedFlightTest",
          nPassengers = 1,
          team = Team(listOf()),
          flightType = FlightType.FONDUE,
          balloon = Balloon("test", BalloonQualification.MEDIUM),
          basket = Basket("test", true),
          date = LocalDate.now(),
          timeSlot = if (LocalTime.now().hour < 12) TimeSlot.AM else TimeSlot.PM,
          vehicles = listOf(),
          remarks = listOf(),
          meetupTimeTeam = LocalTime.now(),
          departureTimeTeam = LocalTime.now(),
          meetupTimePassenger = LocalTime.now(),
          meetupLocationPassenger = "test")

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
      inFlightViewModel.init(dbSetup.crew1.id).join()
      composeTestRule.setContent {
        navController = TestNavHostController(LocalContext.current)
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        viewModel = FlightsViewModel.createViewModel(repository, dbSetup.crew1.id)
        LaunchFlight(navController, viewModel, inFlightViewModel)
      }
      composeTestRule.onNodeWithText("No flight started").assertExists()
    }
  }

  @Test
  fun checkPilotNoFlightConfirmed() {
    runTest {
      inFlightViewModel.init(dbSetup.pilot3.id).join()
      composeTestRule.setContent {
        navController = TestNavHostController(LocalContext.current)
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        viewModel = FlightsViewModel.createViewModel(repository, dbSetup.pilot3.id)
        LaunchFlight(navController, viewModel, inFlightViewModel)
      }
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
      inFlightViewModel.init(dbSetup.pilot1.id).join()
      composeTestRule.setContent {
        navController = TestNavHostController(LocalContext.current)
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        viewModel = FlightsViewModel.createViewModel(repository, dbSetup.pilot1.id)
        LaunchFlight(navController, viewModel, inFlightViewModel)
      }
      composeTestRule.onNodeWithTag("flightCard${dbSetup.flight4.id}").assertExists()
    }
  }

  @Test
  fun checkPilotFlightLaunched() {
    runTest {
      inFlightViewModel.init(dbSetup.pilot1.id).join()
      inFlightViewModel.startFlight().join()
      composeTestRule.setContent {
        navController = TestNavHostController(LocalContext.current)
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        viewModel = FlightsViewModel.createViewModel(repository, dbSetup.pilot1.id)
        LaunchFlight(navController, viewModel, inFlightViewModel)
      }
      val route = navController.currentBackStackEntry?.destination?.route
      Assert.assertEquals(route, Route.FLIGHT)
    }
  }

  @Test
  fun checkCrewFlightLaunched() {
    runTest {
      inFlightViewModel.init(dbSetup.crew1.id).join()
      inFlightViewModel.startFlight().join()
      composeTestRule.setContent {
        navController = TestNavHostController(LocalContext.current)
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        viewModel = FlightsViewModel.createViewModel(repository, dbSetup.crew1.id)
        LaunchFlight(navController, viewModel, inFlightViewModel)
      }
      val route = navController.currentBackStackEntry?.destination?.route
      Assert.assertEquals(route, Route.FLIGHT)
    }
  }
}
