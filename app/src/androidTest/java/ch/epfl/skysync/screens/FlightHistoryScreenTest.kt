package ch.epfl.skysync.screens

import android.location.Location
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FlightHistoryScreenTest {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController

  @Before
  fun setupHistory() {
    composeTestRule.setContent {
      val allFlights: MutableList<FinishedFlight> = remember {
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
                takeOffTime = LocalTime.now(),
                landingTime = LocalTime.now(),
                takeOffLocation = Location("Lausanne"),
                landingLocation = Location("Lausanne")),
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
                takeOffTime = LocalTime.now(),
                landingTime = LocalTime.now(),
                takeOffLocation = Location("Lausanne"),
                landingLocation = Location("Lausanne")))
      }
      navController = TestNavHostController(LocalContext.current)
      FlightHistoryScreen(navController, allFlights)
    }
  }

  @Test
  fun filtersMenuAppearCorrectly() {
    composeTestRule.onNodeWithTag("Filter Button").performClick()
    composeTestRule.onNodeWithTag("Filter Menu").assertIsDisplayed()
  }

  @Test
  fun twoCardAreInitiallyDisplayed() {
    composeTestRule.onNodeWithTag("Card 0").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Card 1").assertIsDisplayed()
  }
}
