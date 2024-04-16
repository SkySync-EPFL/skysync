package ch.epfl.skysync.model.calendar

import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.FlightGroup
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TestFlightGroup {

  lateinit var testFlight: PlannedFlight
  lateinit var emptyGroup: FlightGroup

  @Before
  fun setUp() {
    emptyGroup = FlightGroup(LocalDate.of(2024, 4, 1), TimeSlot.AM, emptyList())
    testFlight =
        PlannedFlight(
            UNSET_ID,
            1,
            FlightType.FONDUE,
            balloon = null,
            basket = null,
            date = LocalDate.of(2024, 4, 1),
            timeSlot = TimeSlot.AM,
            vehicles = listOf())
  }

  @Test
  fun `addFlight adds new flight to current`() {
    val newGroup = emptyGroup.addFlight(testFlight)
    assertEquals(newGroup.flights.size, 1)
    assertEquals(newGroup.flights[0], testFlight)
  }

  @Test
  fun `isEmpty is true if no flights are in the group `() {
    assertTrue(emptyGroup.isEmpty())
  }
}
