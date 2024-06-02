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
   * Changes the status of the availability of given date and timeSlot if found in the calendar.
   *
   * @param date The date of the availability to change.
   * @param timeSlot The timeSlot of the availability to change.
   * @param status The new status.
   * @return The updated calendar model.
   */
  fun setAvailabilityByDate(
      date: LocalDate,
      timeSlot: TimeSlot,
      status: AvailabilityStatus
  ): CalendarModel<Availability> {
    return setByDate(date, timeSlot) { d, t, old ->
      old?.copy(status = status) ?: Availability(UNSET_ID, status, t, d)
    }
  }

  /**
   * Returns current AvailabilityStatus for given date and timeSlot if any, else UNDEFINED.
   *
   * @param date The date to check.
   * @param timeSlot The timeSlot to check.
   * @return The availability status.
   */
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
  fun setToNextAvailabilityStatus(date: LocalDate, timeSlot: TimeSlot): AvailabilityCalendar {
    val currentAvailability = getByDate(date, timeSlot)
    val nextAvailabilityStatus: AvailabilityStatus =
        currentAvailability?.status?.next()
            ?: AvailabilityStatus.OK // non-existing entries get init by OK
    return if (nextAvailabilityStatus != AvailabilityStatus.ASSIGNED) {
      setAvailabilityByDate(date, timeSlot, nextAvailabilityStatus) as AvailabilityCalendar
    } else {
      this
    }
  }

  /**
   * Returns the differences between this and the other calendar, from the point of view of the
   * other calendar (that is, [CalendarDifferenceType.ADDED] means that [newCalendar] has an added
   * availability compared to this).
   *
   * @param newCalendar The other calendar.
   * @return The list of differences.
   */
  fun getDifferencesWithOtherCalendar(
      newCalendar: AvailabilityCalendar
  ): List<Pair<CalendarDifferenceType, Availability>> {
    val differences = mutableListOf<Pair<CalendarDifferenceType, Availability>>()
    for (newCalendarAvailability in newCalendar.cells) {
      val date = newCalendarAvailability.date
      val timeSlot = newCalendarAvailability.timeSlot
      val oldCalendarAvailability = getByDate(date, timeSlot)
      if (newCalendarAvailability.status == oldCalendarAvailability?.status) {
        continue
      }
      if (oldCalendarAvailability?.status == AvailabilityStatus.UNDEFINED) {
        throw IllegalStateException("oldCalendarAvailability.status == UNDEFINED")
      }
      if (oldCalendarAvailability == null) {
        if (newCalendarAvailability.status != AvailabilityStatus.UNDEFINED) {
          differences.add(Pair(CalendarDifferenceType.ADDED, newCalendarAvailability))
        }
        // if NewCalendarAvailability.status == UNDEFINED, then it is was temporarily added
        // and deleted again before saving
      } else if (newCalendarAvailability.status == AvailabilityStatus.UNDEFINED) {
        differences.add(Pair(CalendarDifferenceType.DELETED, oldCalendarAvailability))
      } else {
        differences.add(Pair(CalendarDifferenceType.UPDATED, newCalendarAvailability))
      }
    }
    return differences
  }

  /**
   * Returns a copy of the current AvailabilityCalendar.
   *
   * @return The copied AvailabilityCalendar.
   */
  fun copy(): AvailabilityCalendar {
    return AvailabilityCalendar(cells)
  }

  /**
   * Constructs a new instance of the [CalendarModel] with the given cells.
   *
   * @param cells The list of availabilities to be included in the new calendar model.
   * @return A new instance of [CalendarModel] with the given cells.
   */
  override fun constructor(cells: List<Availability>): CalendarModel<Availability> {
    return AvailabilityCalendar(cells)
  }

  /**
   * Returns a new AvailabilityCalendar with the given cells added.
   *
   * @param elementsToAdd The cells to add.
   * @return The new AvailabilityCalendar.
   */
  override fun addCells(elementsToAdd: List<Availability>): AvailabilityCalendar {
    return super.addCells(elementsToAdd) as AvailabilityCalendar
  }
}
