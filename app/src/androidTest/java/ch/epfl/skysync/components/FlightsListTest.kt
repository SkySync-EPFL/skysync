package ch.epfl.skysync.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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

  @Test
  fun flightsAreCorrectlySorted() {
    val flight1 =
        PlannedFlight(
            nPassengers = 1,
            date = LocalDate.of(2023, 1, 1),
            timeSlot = TimeSlot.AM,
            flightType = FlightType.DISCOVERY,
            vehicles = emptyList(),
            balloon = null,
            basket = null,
            id = "flight1")

    val flight2 =
        PlannedFlight(
            nPassengers = 1,
            date = LocalDate.of(2023, 1, 2),
            timeSlot = TimeSlot.AM,
            flightType = FlightType.DISCOVERY,
            vehicles = emptyList(),
            balloon = null,
            basket = null,
            id = "flight2")

    val flights = listOf(flight2, flight1)

    composeTestRule.setContent {
      FlightsList(
          flights = flights, color = lightOrange, padding, "Upcoming Flights", onFlightClick = {})
    }

    // Check if the flights are displayed in the correct order
    val node1 = composeTestRule.onNode(hasTestTag("flightCardflight1"))
    val node2 = composeTestRule.onNode(hasTestTag("flightCardflight2"))
    node1.assertIsDisplayed()
    node2.assertIsDisplayed()
    val topNode1 = node1.fetchSemanticsNode().boundsInRoot.top
    val topNode2 = node2.fetchSemanticsNode().boundsInRoot.top
    assertTrue(topNode1 < topNode2)
  }
}
