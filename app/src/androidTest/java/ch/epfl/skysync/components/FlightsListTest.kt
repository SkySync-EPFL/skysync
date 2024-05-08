package ch.epfl.skysync.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.skysync.components.UpcomingFlights
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.ui.theme.lightOrange
import java.time.LocalDate
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FlightListsTests {

  @get:Rule val composeTestRule = createComposeRule()

  val padding = PaddingValues()

  @Test
  fun upcomingFlightsDisplaysNoFlightsWhenListIsEmpty() {
    composeTestRule.setContent {
      FlightsList(
          flights = emptyList(),
          color = lightOrange,
          padding,
          "Upcoming Flights",
          onFlightClick = {})
    }

    composeTestRule.onNodeWithText("No flights").assertIsDisplayed()
  }

  @Test
  fun upcomingFlightsDisplaysFlightsWhenListIsNotEmpty() {
    val testFlight =
        PlannedFlight(
            // Assuming your Flight data class constructor and properties
            nPassengers = 1,
            date = LocalDate.now(),
            timeSlot = TimeSlot.AM,
            flightType = FlightType.DISCOVERY, // Assuming enum or similar for flight types
            vehicles = emptyList(),
            balloon = null,
            basket = null,
            id = "testFlightId")

    composeTestRule.setContent {
      FlightsList(
          flights = listOf(testFlight),
          color = lightOrange,
          padding,
          "Upcoming Flights",
          onFlightClick = {})
    }

    composeTestRule.onNodeWithText("Discovery - 1 pax").assertIsDisplayed()
  }

  @Test
  fun clickingOnFlightTriggersCallback() {
    var wasClicked = false
    val testFlight =
        PlannedFlight(
            // Same assumptions as above
            nPassengers = 1,
            date = LocalDate.now(),
            timeSlot = TimeSlot.AM,
            flightType = FlightType.DISCOVERY, // Assuming enum or similar for flight types
            vehicles = emptyList(),
            balloon = null,
            basket = null,
            id = "testFlightId")

    composeTestRule.setContent {
      FlightsList(flights = listOf(testFlight), color = lightOrange, padding, "Upcoming Flights") {
        wasClicked = it == testFlight.id
      }
    }

    composeTestRule.onNodeWithText("Discovery - 1 pax").performClick()

    assertTrue("Flight click callback was not triggered with the correct flight", wasClicked)
  }
}
