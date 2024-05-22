package ch.epfl.skysync.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import java.time.LocalDate
import org.junit.Rule
import org.junit.Test

class FlightCardTest {
  @get:Rule val composeTestRule = createComposeRule()

  val padding = PaddingValues()

  @Test
  fun flightCardDisplaysCorrectData() {
    val flight =
        PlannedFlight(
            nPassengers = 1,
            date = LocalDate.of(2024, 1, 1),
            timeSlot = TimeSlot.AM,
            flightType = FlightType.DISCOVERY,
            vehicles = emptyList(),
            balloon = null,
            basket = null,
            id = "flight1")

    // Set the content to the FlightCard composable
    composeTestRule.setContent { FlightCard(flight = flight, onFlightClick = {}) }
    composeTestRule.onNodeWithText("Mon\nJan 01").assertExists()
  }
}
