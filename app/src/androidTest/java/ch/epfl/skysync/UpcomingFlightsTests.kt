package ch.epfl.skysync

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performClick
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.screens.UpcomingFlights
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import org.junit.Assert.assertTrue

class UpcomingFlightsTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun upcomingFlightsDisplaysNoFlightsWhenListIsEmpty() {
        composeTestRule.setContent {
            UpcomingFlights(flights = emptyList(), onFlightClick = {})
        }

        composeTestRule.onNodeWithText("No upcoming flights").assertIsDisplayed()
    }

    @Test
    fun upcomingFlightsDisplaysFlightsWhenListIsNotEmpty() {
        val testFlight = PlannedFlight(
            // Assuming your Flight data class constructor and properties
            nPassengers = 1,
            date = LocalDate.now(),
            timeSlot = TimeSlot.AM,
            flightType = FlightType.DISCOVERY, // Assuming enum or similar for flight types
            vehicles = emptyList(),
            balloon = null,
            basket = null,
            id = "testFlightId"
        )

        composeTestRule.setContent {
            UpcomingFlights(flights = listOf(testFlight), onFlightClick = {})
        }

        composeTestRule.onNodeWithText("DISCOVERY - 1 pax").assertIsDisplayed()
    }

    @Test
    fun clickingOnFlightTriggersCallback() {
        var wasClicked = false
        val testFlight = PlannedFlight(
            // Same assumptions as above
            nPassengers = 1,
            date = LocalDate.now(),
            timeSlot = TimeSlot.AM,
            flightType = FlightType.DISCOVERY, // Assuming enum or similar for flight types
            vehicles = emptyList(),
            balloon = null,
            basket = null,
            id = "testFlightId"
        )

        composeTestRule.setContent {
            UpcomingFlights(flights = listOf(testFlight)) {
                wasClicked = it.id == testFlight.id
            }
        }

        composeTestRule.onNodeWithText("DISCOVERY - 1 pax").performClick()

        assertTrue("Flight click callback was not triggered with the correct flight", wasClicked)
    }
}
