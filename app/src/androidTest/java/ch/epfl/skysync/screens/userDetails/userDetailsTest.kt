package ch.epfl.skysync.screens.userDetails

import android.location.Location
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.screens.UserDetailsScreen
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Rule
import org.junit.Test

class userDetailsTest {
  @get:Rule val composeTestRule = createComposeRule()

  private val dbs = DatabaseSetup()

  private val user = dbs.pilot1

  private val role = Role(RoleType.PILOT, user)

  private val team = Team(listOf(role))

  lateinit var navController: TestNavHostController

  private var allFlights: MutableList<FinishedFlight> =
      mutableStateListOf(
          FinishedFlight(
              id = "testFlightId",
              nPassengers = 0,
              team = team,
              flightType = FlightType.DISCOVERY,
              balloon = Balloon("Balloon", BalloonQualification.MEDIUM),
              basket = Basket("Basket", true),
              date = LocalDate.now(),
              timeSlot = TimeSlot.AM,
              vehicles = emptyList(),
              flightTime = 0L,
              takeOffTime = LocalTime.now(),
              landingTime = LocalTime.now(),
              takeOffLocation = Location("Lausanne 1"),
              landingLocation = Location("Lausanne")),
      )

  @Test
  fun testPersonalFlightHistoryDisplaysFlights() {
    composeTestRule.setContent {
      UserDetailsScreen(
          navController = rememberNavController(), allFlights = allFlights, user = user)
    }
    // Check for elements
    composeTestRule.onNodeWithText("Completed Flights").assertIsDisplayed()
    composeTestRule.onNodeWithTag("flightCard").assertExists()
  }

  @Test
  fun testEmptyMessage() {
    composeTestRule.setContent {
      UserDetailsScreen(navController = rememberNavController(), allFlights = listOf(), user = user)
    }
    // Check for elements
    composeTestRule.onNodeWithText("No completed flights").assertIsDisplayed()
  }

  @Test
  fun testUserNameIsDisplayed() {
    composeTestRule.setContent {
      UserDetailsScreen(
          navController = rememberNavController(), allFlights = allFlights, user = user)
    }
    // Check for elements
    composeTestRule.onNodeWithText("${user.firstname} ${user.lastname}").assertIsDisplayed()
  }
}
