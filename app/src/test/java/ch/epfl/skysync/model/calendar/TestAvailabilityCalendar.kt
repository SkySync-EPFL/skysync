package ch.epfl.skysync.model.calendar

import ch.epfl.skysync.models.calendar.Availability
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.TimeSlot
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TestAvailabilityCalendar {
  var defaultAvailabilities = listOf<Availability>()
  var calendar = AvailabilityCalendar()
  var someDate = LocalDate.of(2024, 4, 1)

  @Before
  fun setUp() {
    someDate = LocalDate.of(2024, 4, 1)
    val defaultAvailability1 = Availability("1", AvailabilityStatus.OK, TimeSlot.AM, someDate)
    val defaultAvailability2 =
        Availability("2", AvailabilityStatus.MAYBE, TimeSlot.PM, someDate.plusDays(1))
    defaultAvailabilities = listOf(defaultAvailability1, defaultAvailability2)
    calendar = AvailabilityCalendar()
  }

  @Test
  fun `add element`() {
    val calendar = AvailabilityCalendar()
    assertEquals(calendar.cells, emptyList<Availability>())
    val availability = Availability("1", AvailabilityStatus.OK, TimeSlot.AM, someDate)
    val newList = calendar.add(availability)
    assertEquals(newList, listOf(availability))
    assertEquals(calendar.cells, emptyList<Availability>())
  }

  @Test
  fun `finds an availability status by date and time slot`() {
    val calendar = AvailabilityCalendar()
    calendar.addCells(defaultAvailabilities)
    val av1 = defaultAvailabilities[0]
    val av2 = defaultAvailabilities[1]
    assertEquals(calendar.getAvailabilityStatus(av1.date, av1.timeSlot), av1.status)
    assertEquals(calendar.getAvailabilityStatus(av2.date, av2.timeSlot), av2.status)
  }

  @Test
  fun `add a new availability by date and time slot`() {
    val calendar = AvailabilityCalendar()
    val newStatus = AvailabilityStatus.OK
    val timeSlot = TimeSlot.PM
    calendar.setAvailabilityByDate(someDate, timeSlot, newStatus)
    assertEquals(calendar.getAvailabilityStatus(someDate, timeSlot), newStatus)
  }

  @Test
  fun `change an availability status by date and time slot without changing others`() {
    val initAvailability = Availability("1", AvailabilityStatus.MAYBE, TimeSlot.AM, someDate)
    val initAvailability2 = Availability("1", AvailabilityStatus.OK, TimeSlot.PM, someDate)
    calendar.addCells(listOf(initAvailability, initAvailability2))
    val new_status = AvailabilityStatus.NO
    calendar.setAvailabilityByDate(initAvailability.date, initAvailability.timeSlot, new_status)
    // check availability is changed
    assertEquals(
        calendar.getAvailabilityStatus(initAvailability.date, initAvailability.timeSlot),
        new_status)
    // check other availability is not changed
    assertEquals(
        calendar.getAvailabilityStatus(initAvailability2.date, initAvailability2.timeSlot),
        AvailabilityStatus.OK)
  }

  @Test
  fun `nextAvailabilityStatus updates existing entry to next status and returns correctly`() {
    val calendar = AvailabilityCalendar()
    val availability1 = Availability("1", AvailabilityStatus.MAYBE, TimeSlot.AM, someDate)
    calendar.addCells(listOf(availability1))
    assertEquals(
        calendar.nextAvailabilityStatus(availability1.date, availability1.timeSlot),
        availability1.status.next())
  }

  @Test
  fun `nextAvailabilityStatus initialises non-existing entry to OK and returns correctly`() {
    val calendar = AvailabilityCalendar()
    val availability1 = Availability("1", AvailabilityStatus.MAYBE, TimeSlot.AM, someDate)
    assertEquals(
        calendar.nextAvailabilityStatus(availability1.date, availability1.timeSlot),
        AvailabilityStatus.OK)
  }

  @Test
  fun `nextAvailabilityStatus removes NO and returns UNDEFINED`() {
    val calendar = AvailabilityCalendar()
    val availability1 = Availability("1", AvailabilityStatus.NO, TimeSlot.AM, someDate)
    calendar.addCells(listOf(availability1))
    assertEquals(
        calendar.nextAvailabilityStatus(availability1.date, availability1.timeSlot),
        AvailabilityStatus.UNDEFINED)
    assertEquals(calendar.getSize(), 0)
  }

  @Test
  fun `size is correctly returned`() {
    val calendar = AvailabilityCalendar()
    val availability1 = Availability("1", AvailabilityStatus.NO, TimeSlot.AM, someDate)
    calendar.addCells(listOf(availability1))
    assertEquals(calendar.getSize(), 1)
    val availability2 = Availability("2", AvailabilityStatus.MAYBE, TimeSlot.PM, someDate)
    calendar.addCells(listOf(availability2))
    assertEquals(calendar.getSize(), 2)
  }
}
