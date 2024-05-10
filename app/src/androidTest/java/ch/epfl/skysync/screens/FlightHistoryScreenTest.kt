package ch.epfl.skysync.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.BASE_ROLES
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.location.LocationPoint
import ch.epfl.skysync.screens.admin.FlightHistoryScreen
import java.time.Instant
import java.time.LocalDate
import java.util.Date
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FlightHistoryScreenTest {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController
  private var allFlights: MutableList<FinishedFlight> =
      mutableStateListOf(
          FinishedFlight(
              id = UNSET_ID,
              nPassengers = 0,
              team = Team(Role.initRoles(BASE_ROLES)),
              flightType = FlightType.DISCOVERY,
              balloon = Balloon("Balloon", BalloonQualification.MEDIUM),
              basket = Basket("Basket", true),
              date = LocalDate.now(),
              timeSlot = TimeSlot.AM,
              vehicles = emptyList(),
              flightTime = 0L,
              takeOffTime = Date.from(Instant.now()),
              landingTime = Date.from(Instant.now()),
              takeOffLocation =
                  LocationPoint(time = 0, latitude = 0.0, longitude = 0.0, name = "test1"),
              landingLocation =
                  LocationPoint(time = 0, latitude = 1.0, longitude = 1.0, name = "test1_value2")),
          FinishedFlight(
              id = UNSET_ID,
              nPassengers = 0,
              team = Team(Role.initRoles(BASE_ROLES)),
              flightType = FlightType.HIGH_ALTITUDE,
              balloon = Balloon("Balloon", BalloonQualification.MEDIUM),
              basket = Basket("Basket", true),
              date = LocalDate.now(),
              timeSlot = TimeSlot.AM,
              vehicles = emptyList(),
              flightTime = 0L,
              takeOffTime = Date.from(Instant.now()),
              landingTime = Date.from(Instant.now()),
              takeOffLocation =
                  LocationPoint(time = 0, latitude = 0.0, longitude = 0.0, name = "test2"),
              landingLocation =
                  LocationPoint(time = 0, latitude = 1.0, longitude = 1.0, name = "test2_value2")))

  @Before fun setupHistory() {}

  // TODO set content before when viewModel is implemented
  @Test
  fun filtersMenuAppearCorrectly() {
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      FlightHistoryScreen(navController, allFlights)
    }
    composeTestRule.onNodeWithTag("Filter Button").performClick()
    composeTestRule.onNodeWithTag("Filter Menu").assertIsDisplayed()
  }

  @Test
  fun twoCardAreInitiallyDisplayed() {
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      FlightHistoryScreen(navController, allFlights)
    }
    composeTestRule.onNodeWithTag("Card 0").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Card 1").assertIsDisplayed()
  }

  @Test
  fun searchBarWorksCorrectly() {
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      FlightHistoryScreen(navController, allFlights)
    }
    composeTestRule.onNodeWithTag("Search Bar").onChildAt(0).performTextInput("Lausanne 1")
    // Not implemented yet
    /*composeTestRule.onNodeWithTag("Card 0").assertExists()
    composeTestRule.onNodeWithTag("Card 1").assertDoesNotExist()*/
  }

  @Test
  fun rangeDateSelectorShowsWorksCorrectly() {
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      FlightHistoryScreen(navController, allFlights)
    }
    composeTestRule.onNodeWithTag("Filter Button").performClick()
    composeTestRule.onNodeWithTag("Date Range Field 1").performClick()
    composeTestRule.onNodeWithTag("Date Range Selector").assertIsDisplayed()
  }

  @Test
  fun noFlightIsCorrectlyDisplayed() {
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      FlightHistoryScreen(navController)
    }
    composeTestRule.onNodeWithTag("No Flight").assertIsDisplayed()
  }
}
