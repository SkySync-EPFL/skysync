package ch.epfl.skysync.model.flight

import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TestPlannedFlight {

  @Test
  fun `planned flight is correctly created`() {

    val testFlight =
        PlannedFlight(
            1,
            2,
            Team(listOf()),
            FlightType.DISCOVERY,
            Balloon("QQP", BalloonQualification.LARGE, ""),
            Basket("basket 1", hasDoor = false),
            LocalDate.now(),
            timeSlot = TimeSlot.AM,
            listOf(Vehicle("sprinter 1")))

    assertTrue(true)
  }
}
