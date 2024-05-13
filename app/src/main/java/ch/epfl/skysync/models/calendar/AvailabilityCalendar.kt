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
   * other calendar (that is, [CalendarDifferenceType.ADDED] means that [other] has an added
   * availability compared to this)
   *
   * @param other The other calendar
   */
  fun getDifferencesWithOtherCalendar(
      other: AvailabilityCalendar
  ): List<Pair<CalendarDifferenceType, Availability>> {
    val differences = mutableListOf<Pair<CalendarDifferenceType, Availability>>()
    for (otherAvailability in other.cells) {
      val date = otherAvailability.date
      val timeSlot = otherAvailability.timeSlot
      val thisAvailability = getByDate(date, timeSlot)
      if (otherAvailability == thisAvailability) {
        continue
      }
      if (thisAvailability == null || thisAvailability.status == AvailabilityStatus.UNDEFINED) {
        differences.add(Pair(CalendarDifferenceType.ADDED, otherAvailability))
      } else if (otherAvailability.status == AvailabilityStatus.UNDEFINED) {
        differences.add(Pair(CalendarDifferenceType.DELETED, thisAvailability))
      } else {
        differences.add(Pair(CalendarDifferenceType.UPDATED, otherAvailability))
      }
    }
    for (thisAvailability in cells) {
      val date = thisAvailability.date
      val timeSlot = thisAvailability.timeSlot
      val otherAvailability = other.getByDate(date, timeSlot)
      if (otherAvailability == null) {
        differences.add(Pair(CalendarDifferenceType.DELETED, thisAvailability))
      }
    }
    return differences
  }

  fun copy(): AvailabilityCalendar {
    return AvailabilityCalendar(cells.toMutableList())
  }

  override fun constructor(cells: List<Availability>): CalendarModel<Availability> {
    return AvailabilityCalendar(cells)
  }
}
