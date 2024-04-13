package ch.epfl.skysync.models.calendar

import ch.epfl.skysync.models.UNSET_ID
import java.time.LocalDate

/** represents a calendar for availabilities */
class AvailabilityCalendar(cells: MutableList<Availability> = mutableListOf()) : CalendarModel<Availability>(cells=cells) {

  fun getAvailabilities(): List<Availability> {
    return cells
  }

  /**
   * changes the status of the availability of given date and timeSlot if found in the calendar
   *
   * @param date: the date of the availability to change
   * @param timeSlot: the timeSlot of the availability to change
   * @param status: the new status
   */
  fun setAvailabilityByDate(date: LocalDate, timeSlot: TimeSlot, status: AvailabilityStatus) {
    setByDate(date, timeSlot) { d, t, old ->
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
  fun nextAvailabilityStatus(date: LocalDate, timeSlot: TimeSlot): AvailabilityStatus {
    val currentAvailability = getByDate(date, timeSlot)
    val nextAvailabilityStatus: AvailabilityStatus =
        currentAvailability?.status?.next()
            ?: AvailabilityStatus.OK // non-existing entries get init by OK
    if (nextAvailabilityStatus == AvailabilityStatus.UNDEFINED) {
      removeByDate(date, timeSlot)
    } else {
      setAvailabilityByDate(date, timeSlot, nextAvailabilityStatus)
    }
    return nextAvailabilityStatus
  }

  fun copy():AvailabilityCalendar {
    return AvailabilityCalendar(cells.toMutableList())
  }
}
