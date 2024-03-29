package ch.epfl.skysync.model.calendar

import ch.epfl.skysync.models.calendar.Availability
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.TimeSlot
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TestAvailabilityCalendar {
  var availabilities = listOf<Availability>()
  var calendar = AvailabilityCalendar()

  @Before
  fun setUp() {
    val av1 = Availability("1", AvailabilityStatus.OK, TimeSlot.AM, LocalDate.now())
    val av2 = Availability("2", AvailabilityStatus.OK, TimeSlot.AM, LocalDate.now().plusDays(1))
    availabilities = listOf(av1, av2)
    calendar = AvailabilityCalendar()

  }

  @Test
  fun `finds an availability by date and time slot`() {
    val calendar = AvailabilityCalendar()
    calendar.addCells(availabilities)
    val av1 = availabilities[0]
    val av2 = availabilities[1]
    assertEquals(calendar.getByDate(av1.date, av1.timeSlot), av1)
    assertEquals(calendar.getByDate(av2.date, av2.timeSlot), av2)
  }

  @Test
  fun `change an availability status by date and time slot`() {
    calendar.addCells(availabilities)
    val av1 = availabilities[0]
    val av2 = availabilities[1]
    val new_status = AvailabilityStatus.NO
    calendar.setAvailabilityByDate(av1.date, av1.timeSlot, new_status)
    val av1_expected = Availability(av1.id, new_status, av1.timeSlot, av1.date)
    // check availability is changed
    assertEquals(calendar.getByDate(av1.date, av1.timeSlot), av1_expected)
    // check other availability is not changed
    assertEquals(calendar.getByDate(av2.date, av2.timeSlot), av2)
  }

  @Test
  fun `remove an existing availability by date and time slot`() {
    calendar.addCells(availabilities)
    val av1 = availabilities[0]
    val av2 = availabilities[1]
    assertEquals(calendar.removeByDate(av1.date, av1.timeSlot), av1)
    //check only the first availability is removed
    assertEquals(calendar.getByDate(av2.date, av2.timeSlot), av2)
  }

  @Test
  fun `remove non existing availability by date and time slot`() {
    calendar.addCells(availabilities.take(1))
    val av1 = availabilities[0]
    val av2 = availabilities[1]
    //check that av2 is initially not in the calendar
    assertEquals(calendar.getByDate(av2.date, av2.timeSlot), null)
    //check that removing av2 does not change the calendar
    assertEquals(calendar.removeByDate(av2.date, av2.timeSlot), null)
    assertEquals(calendar.getByDate(av1.date, av1.timeSlot), av1)
  }

  @Test
  fun `init range`() {
    val number_of_days: Long = 7
    val start = LocalDate.of(2021, 1, 1)
    val end = start.plusDays(number_of_days)
    calendar.initForRange(start, end)
    val expectedNumberOfCells = (number_of_days.toInt()+1)* TimeSlot.entries.size
    assertEquals(calendar.size, expectedNumberOfCells)
    for (i in 0..number_of_days) {
      for (timeSlot in TimeSlot.entries) {
        val av = calendar.getByDate(start.plusDays(i), timeSlot)
        assertNotNull(av)
      }
    }
  }

  @Test
  fun `add rejects cells with same data and timeslot`() {
    calendar.addCells(availabilities)
    assertThrows(IllegalArgumentException::class.java) {
      calendar.addCells(availabilities.take(1))
    }
    assertThrows(IllegalArgumentException::class.java) {
      calendar.addCells(availabilities)
    }
  }

  @Test
  fun `getAvailabilityStatus returns current status`() {
    val calendar = AvailabilityCalendar()
    calendar.addCells(availabilities)
    val av1 = availabilities[0]
    assertEquals(calendar.getAvailabilityStatus(av1.date, av1.timeSlot), av1.status)
  }

  @Test
  fun `nextAvailabilityStatus updates to next status and returns correctly`() {
    val calendar = AvailabilityCalendar()
    calendar.addCells(availabilities)
    val av1 = availabilities[0]
    assertEquals(calendar.nextAvailabilityStatus(av1.date, av1.timeSlot), av1.status.next())
    // check calendar is correctly updated
    assertEquals(calendar.getByDate(av1.date, av1.timeSlot)?.status, av1.status.next())
  }

  @Test
  fun `size is correctly updated`() {
    val calendar = AvailabilityCalendar()
    calendar.addCells(availabilities)
    val av1 = availabilities[0]
    assertEquals(calendar.size, availabilities.size)
    calendar.removeByDate(av1.date, av1.timeSlot)
    assertEquals(calendar.size, availabilities.size-1)
  }








}
