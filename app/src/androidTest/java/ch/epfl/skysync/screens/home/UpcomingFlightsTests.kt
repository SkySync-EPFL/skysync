package ch.epfl.skysync.screens.home

import androidx.compose.ui.test.assertIsDisplayed
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

class UpcomingFlightsTests {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun upcomingFlightsDisplaysNoFlightsWhenListIsEmpty() {
    composeTestRule.setContent {
      UpcomingFlights(flights = emptyList(), color = lightOrange, onFlightClick = {})
    }

    composeTestRule.onNodeWithText("No upcoming flights").assertIsDisplayed()
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
      UpcomingFlights(flights = listOf(testFlight), color = lightOrange, onFlightClick = {})
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
      UpcomingFlights(flights = listOf(testFlight), color = lightOrange) {
        wasClicked = it == testFlight.id
      }
    }

    composeTestRule.onNodeWithText("Discovery - 1 pax").performClick()

    assertTrue("Flight click callback was not triggered with the correct flight", wasClicked)
  }

  /*@Test
  fun floatingActionButton_onClick_logsMessage() {
    val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    val flights = mutableListOf<PlannedFlight>()
    composeTestRule.setContent { HomeScreen(navController, flights) }

    // Perform a click on the FAB
    composeTestRule.onNodeWithContentDescription("Add").performClick()

    // This is where you'd verify the expected behavior. Since we can't directly check Logcat output
    // here,
    // consider verifying navigation or state changes instead. For demonstration purposes, we'll
    // assume
    // a successful test if the FAB is clickable, which has already been performed above.
    // In real-world scenarios, replace this with actual verification logic.
  }*/
}
