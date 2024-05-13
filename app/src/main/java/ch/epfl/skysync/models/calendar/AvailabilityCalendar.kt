package ch.epfl.skysync.models.calendar

import ch.epfl.skysync.models.UNSET_ID
import java.time.LocalDate

/** Represents the type of a cell difference between two calendar */
enum class CalendarDifferenceType {
  ADDED,
  UPDATED,
  DELETED,
}

/** represents a calendar for availabilities */
class AvailabilityCalendar(cells: List<Availability> = listOf()) :
    CalendarModel<Availability>(cells = cells) {

  /**
   * changes the status of the availability of given date and timeSlot if found in the calendar
   *
   * @param date: the date of the availability to change
   * @param timeSlot: the timeSlot of the availability to change
   * @param status: the new status
   */
  fun setAvailabilityByDate(date: LocalDate, timeSlot: TimeSlot, status: AvailabilityStatus):CalendarModel<Availability> {
    return setByDate(date, timeSlot) { d, t, old ->
      old?.copy(status = status) ?: Availability(UNSET_ID, status, t, d)
    }
  }

  /** @return current AvailabilityStatus for given date and timeSlot if any, else UNDEFINED */
  fun getAvailabilityStatus(date: LocalDate, timeSlot: TimeSlot): AvailabilityStatus {
    return getByDate(date, timeSlot)?.status ?: AvailabilityStatus.UNDEFINED
  }

  /**
   * changes the AvailabilityStatus of the Availability for the given date and timeSlot (if found)
   * to the next AvailabilityStatus (round robin)
   *
   * @param date: date of the Availability of which to change the status
   * @param timeSlot: timeSlot of the Availability of which to change the status
   * @return the new AvailabilityStatus if successfully modified, else null
   */
  fun setToNextAvailabilityStatus(date: LocalDate, timeSlot: TimeSlot): CalendarModel<Availability> {
    val currentAvailability = getByDate(date, timeSlot)
    val nextAvailabilityStatus: AvailabilityStatus =
        currentAvailability?.status?.next()
            ?: AvailabilityStatus.OK // non-existing entries get init by OK
    return if (nextAvailabilityStatus == AvailabilityStatus.UNDEFINED) {
      removeByDate(date, timeSlot)
    } else {
      setAvailabilityByDate(date, timeSlot, nextAvailabilityStatus)
    }
  }

  /**
   * Returns the differences between this and the other calendar, from the point of view of the
   * other calendar (that is, [CalendarDifferenceType.ADDED] means that [newCalendar] has an added
   * availability compared to this)
   *
   * @param newCalendar The other calendar
   */
  fun getDifferencesWithOtherCalendar(
    newCalendar: AvailabilityCalendar
  ): List<Pair<CalendarDifferenceType, Availability>> {
    val differences = mutableListOf<Pair<CalendarDifferenceType, Availability>>()
    for (newCalendarAvailability in newCalendar.cells) {
      val date = newCalendarAvailability.date
      val timeSlot = newCalendarAvailability.timeSlot
      val oldCalendarAvailability = getByDate(date, timeSlot)
      if (newCalendarAvailability == oldCalendarAvailability) {
        continue
      }
      if (oldCalendarAvailability == null || oldCalendarAvailability.status == AvailabilityStatus.UNDEFINED) {
        differences.add(Pair(CalendarDifferenceType.ADDED, newCalendarAvailability))
      } else if (newCalendarAvailability.status == AvailabilityStatus.UNDEFINED) {
        //should not happen but checked for safety reasons
        differences.add(Pair(CalendarDifferenceType.DELETED, oldCalendarAvailability))
      } else {
        differences.add(Pair(CalendarDifferenceType.UPDATED, newCalendarAvailability))
      }
    }
    for (oldCalendarAvailability in cells) {
      val date = oldCalendarAvailability.date
      val timeSlot = oldCalendarAvailability.timeSlot
      val newCalendarAvailability = newCalendar.getByDate(date, timeSlot)
      if (newCalendarAvailability == null) {
        differences.add(Pair(CalendarDifferenceType.DELETED, oldCalendarAvailability))
      }
    }
    return differences
  }

  fun copy(): AvailabilityCalendar {
    return AvailabilityCalendar(cells)
  }

  override fun constructor(cells: List<Availability>): CalendarModel<Availability> {
    return AvailabilityCalendar(cells)
  }
}
