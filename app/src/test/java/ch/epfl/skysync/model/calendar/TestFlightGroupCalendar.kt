package ch.epfl.skysync.model.calendar

import ch.epfl.skysync.models.calendar.FlightGroup
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Flight
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
class TestFlightGroupCalendar {
  lateinit var calendar: FlightGroupCalendar
  lateinit var testFlight: Flight
  lateinit var testFlight2: Flight
  lateinit var emptyFlightGroup: FlightGroup

  @Before
  fun setUp() {
    testFlight =
        PlannedFlight(
            "y",
            1,
            FlightType.FONDUE,
            balloon = null,
            basket = null,
            date = LocalDate.of(2024, 4, 1),
            timeSlot = TimeSlot.AM,
            vehicles = listOf())
    testFlight2 =
        PlannedFlight(
            "x",
            2,
            FlightType.DISCOVERY,
            basket = null,
            balloon = null,
            date = LocalDate.of(2024, 4, 1),
            timeSlot = TimeSlot.AM,
            vehicles = listOf())
    emptyFlightGroup = FlightGroup(LocalDate.of(2024, 4, 1), TimeSlot.AM, listOf())
    calendar = FlightGroupCalendar()
  }

  @Test
  fun `getFirstFlightByDate() returns null if no flights found `() {

    calendar.addCells(listOf(emptyFlightGroup))
    calendar.getFirstFlightByDate(emptyFlightGroup.date, emptyFlightGroup.timeSlot)
    assertEquals(
        calendar.getFirstFlightByDate(emptyFlightGroup.date, emptyFlightGroup.timeSlot), null)
  }

  @Test
  fun `getFlightGroupByDate() returns null if no flight found  for date`() {
    val someDate = LocalDate.of(2024, 4, 1)
    assertNull(calendar.getFlightGroupByDate(someDate, TimeSlot.AM))
  }

  @Test
  fun `getFlightGroupByDate() returns the flight group if found`() {
    val flightGroup = FlightGroup(testFlight.date, testFlight.timeSlot, listOf(testFlight))
    calendar.addCells(listOf(flightGroup))
    val foundFlightGroup = calendar.getFlightGroupByDate(testFlight.date, testFlight.timeSlot)
    assertEquals(foundFlightGroup, flightGroup)
  }

  @Test
  fun `getFirstFlightByDate() returns first flight if flight found`() {
    calendar.addCells(listOf(FlightGroup(testFlight.date, testFlight.timeSlot, listOf(testFlight))))
    val foundFlight = calendar.getFirstFlightByDate(testFlight.date, testFlight.timeSlot)
    assertNotNull(foundFlight)
    if (foundFlight != null) {
      assertEquals(foundFlight, testFlight)
    }
  }

  @Test
  fun `add flight to an existing flight group`() {
    calendar.addCells(listOf(FlightGroup(testFlight.date, testFlight.timeSlot, listOf(testFlight))))
    calendar.addFlightByDate(testFlight2.date, testFlight2.timeSlot, testFlight2)
    assertEquals(calendar.getSize(), 1)
    val foundGroup = calendar.getFlightGroupByDate(testFlight2.date, testFlight2.timeSlot)
    assertNotNull(foundGroup)
    if (foundGroup != null) {
      assertTrue(foundGroup.flights.contains(testFlight2))
      assertTrue(foundGroup.flights.contains(testFlight))
    }
  }
}
