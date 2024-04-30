package ch.epfl.skysync.screens

import android.location.Location
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.BASE_ROLES
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.flight.FlightColor
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Rule
import org.junit.Test

class FlightHistoryScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun instantiateFinishedFlight() {
    val finishedFlight =
        FinishedFlight(
            "id",
            1,
            Team(Role.initRoles(BASE_ROLES)),
            FlightType.DISCOVERY,
            Balloon("Balloon", BalloonQualification.MEDIUM),
            Basket("Basket", true),
            LocalDate.now(),
            TimeSlot.AM,
            listOf(Vehicle("Vehicle")),
            FlightColor.NO_COLOR,
            LocalTime.now(),
            Location("Lausanne 1"),
            LocalTime.now(),
            Location("Lausanne 2"),
            0L)
  }
}
