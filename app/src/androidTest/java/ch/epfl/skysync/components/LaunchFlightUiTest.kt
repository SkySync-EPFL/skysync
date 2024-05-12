package ch.epfl.skysync.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.Team
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class LaunchFlightUiTest {
    @get:Rule val composeTestRule = createComposeRule()
    val dummyFlight =
        ConfirmedFlight(
            id = "1",
            nPassengers = 1,
            team = Team(listOf()),
            flightType = FlightType.DISCOVERY,
            balloon = Balloon("",BalloonQualification.MEDIUM),
            basket = Basket("",true),
            date = LocalDate.now(),
            timeSlot = TimeSlot.AM,
            vehicles = listOf(),
            remarks = listOf(),
            meetupTimeTeam = LocalTime.now(),
            departureTimeTeam = LocalTime.now(),
            meetupTimePassenger = LocalTime.now(),
            meetupLocationPassenger = "test",
        )

    @Test
    fun TitleIsDisplayed() {
        composeTestRule.setContent {
            LaunchFlightUi(pilotBoolean = true, flight = dummyFlight, paddingValues = PaddingValues(16.dp)) {}
        }
        composeTestRule.onNodeWithText("Launch Flight").assertIsDisplayed()
    }
    @Test
    fun NoFlightReadyToBeLaunched() {
        composeTestRule.setContent {
            LaunchFlightUi(pilotBoolean = true, flight = null, paddingValues = PaddingValues(16.dp)) {}
        }
        composeTestRule.onNodeWithText("No flight ready to be launched").assertIsDisplayed()
    }
    @Test
    fun FlightReadyToBeLaunched() {
        composeTestRule.setContent {
            LaunchFlightUi(pilotBoolean = true, flight = dummyFlight, paddingValues = PaddingValues(16.dp)) {}
        }
        composeTestRule.onNodeWithTag("flightCard1").assertIsDisplayed()
    }
    @Test
    fun NoFlightStarted() {
        composeTestRule.setContent {
            LaunchFlightUi(pilotBoolean = false, flight = null, paddingValues = PaddingValues(16.dp)) {}
        }
        composeTestRule.onNodeWithText("No flight started").assertIsDisplayed()
    }
}