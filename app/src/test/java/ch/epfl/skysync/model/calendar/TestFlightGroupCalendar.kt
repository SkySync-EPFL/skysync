package ch.epfl.skysync.model.calendar

import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.FlightGroup
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Team
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
  lateinit var emptyFlightGroup: FlightGroup

  @Before
  fun setUp() {
    testFlight =
        PlannedFlight(
            UNSET_ID,
            1,
            Team(listOf()),
            FlightType.FONDUE,
            null,
            null,
            LocalDate.of(2024, 4, 1),
            TimeSlot.AM,
            listOf())
    emptyFlightGroup = FlightGroup(LocalDate.of(2024, 4, 1), TimeSlot.AM, listOf())
    calendar = FlightGroupCalendar()
  }

  @Test
  fun `initForRange() inits empty groups for interval `() {
    val start = LocalDate.of(2024, 4, 1)
    val days: Long = 7
    calendar.initForRange(start, start.plusDays(days - 1))
    assertEquals(calendar.getSize(), days.toInt() * TimeSlot.entries.size)
    for (i in 0 until days) {
      for (timeSlot in TimeSlot.entries) {
        val group = calendar.getByDate(start.plusDays(i), timeSlot)
        assertNotNull(group)
        if (group != null) {
          assertTrue(group.isEmpty())
        }
      }
    }
  }

  @Test
  fun `getFlightByDate() returns null if no flights found `() {

    calendar.addCells(listOf(emptyFlightGroup))
    calendar.getFlightByDate(emptyFlightGroup.date, emptyFlightGroup.timeSlot)
    assertEquals(calendar.getFlightByDate(emptyFlightGroup.date, emptyFlightGroup.timeSlot), null)
  }

  @Test
  fun `getFlightByDate() returns first flight if flight found`() {
    calendar.addCells(listOf(FlightGroup(testFlight.date, testFlight.timeSlot, listOf(testFlight))))
    val foundFlight = calendar.getFlightByDate(testFlight.date, testFlight.timeSlot)
    assertNotNull(foundFlight)
    if (foundFlight != null) {
      assertEquals(foundFlight, testFlight)
    }
  }

  //  @Test
  //  fun `remove an existing availability by date and time slot`() {
  //    calendar.addCells(availabilities)
  //    val av1 = availabilities[0]
  //    val av2 = availabilities[1]
  //    assertEquals(calendar.removeByDate(av1.date, av1.timeSlot), av1)
  //    // check only the first availability is removed
  //    assertEquals(calendar.getByDate(av2.date, av2.timeSlot), av2)
  //  }
  //
  //  @Test
  //  fun `remove non existing availability by date and time slot`() {
  //    calendar.addCells(availabilities.take(1))
  //    val av1 = availabilities[0]
  //    val av2 = availabilities[1]
  //    // check that av2 is initially not in the calendar
  //    assertEquals(calendar.getByDate(av2.date, av2.timeSlot), null)
  //    // check that removing av2 does not change the calendar
  //    assertEquals(calendar.removeByDate(av2.date, av2.timeSlot), null)
  //    assertEquals(calendar.getByDate(av1.date, av1.timeSlot), av1)
  //  }
  //
  //  @Test
  //  fun `init range`() {
  //    val number_of_days: Long = 7
  //    val start = LocalDate.of(2021, 1, 1)
  //    val end = start.plusDays(number_of_days)
  //    calendar.initForRange(start, end)
  //    val expectedNumberOfCells = (number_of_days.toInt() + 1) * TimeSlot.entries.size
  //    assertEquals(calendar.size, expectedNumberOfCells)
  //    for (i in 0..number_of_days) {
  //      for (timeSlot in TimeSlot.entries) {
  //        val av = calendar.getByDate(start.plusDays(i), timeSlot)
  //        assertNotNull(av)
  //      }
  //    }
  //  }
  //
  //  @Test
  //  fun `add rejects cells with same data and timeslot`() {
  //    calendar.addCells(availabilities)
  //    assertThrows(IllegalArgumentException::class.java) {
  // calendar.addCells(availabilities.take(1)) }
  //    assertThrows(IllegalArgumentException::class.java) { calendar.addCells(availabilities) }
  //  }
  //
  //  @Test
  //  fun `getAvailabilityStatus returns current status`() {
  //    val calendar = AvailabilityCalendar()
  //    calendar.addCells(availabilities)
  //    val av1 = availabilities[0]
  //    assertEquals(calendar.getAvailabilityStatus(av1.date, av1.timeSlot), av1.status)
  //  }
  //
  //  @Test
  //  fun `nextAvailabilityStatus updates to next status and returns correctly`() {
  //    val calendar = AvailabilityCalendar()
  //    calendar.addCells(availabilities)
  //    val av1 = availabilities[0]
  //    assertEquals(calendar.nextAvailabilityStatus(av1.date, av1.timeSlot), av1.status.next())
  //    // check calendar is correctly updated
  //    assertEquals(calendar.getByDate(av1.date, av1.timeSlot)?.status, av1.status.next())
  //  }
  //
  //  @Test
  //  fun `size is correctly updated`() {
  //    val calendar = AvailabilityCalendar()
  //    calendar.addCells(availabilities)
  //    val av1 = availabilities[0]
  //    assertEquals(calendar.size, availabilities.size)
  //    calendar.removeByDate(av1.date, av1.timeSlot)
  //    assertEquals(calendar.size, availabilities.size - 1)
  //  }
}
