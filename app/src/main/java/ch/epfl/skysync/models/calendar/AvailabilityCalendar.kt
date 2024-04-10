package ch.epfl.skysync.models.calendar

import ch.epfl.skysync.models.UNSET_ID
import java.time.LocalDate

/** represents a calendar for availabilities */
class AvailabilityCalendar : CalendarModel<Availability>() {

  /**
   * changes the status of the availability of given date and timeSlot if found in the calendar
   *
   * @param date: the date of the availability to change
   * @param timeSlot: the timeSlot of the availability to change
   * @param status: the new status
   */
  fun setAvailabilityByDate(date: LocalDate, timeSlot: TimeSlot, status: AvailabilityStatus) {
    setByDate(date, timeSlot) { d, t, old -> old?.copy(status = status)?: Availability(UNSET_ID, status, t, d)}
  }

  /**
   * fills the calendar with availabilities for the given interval. Each availability is init with
   * UNSET_ID and MAYBE
   *
   * @param from: the first date for which an entry in the calendar is initialized
   * @param to: the last date (inclusive)
   */
  override fun initForRange(from: LocalDate, to: LocalDate) {
    initForRangeSuper(from, to) { date, timeSlot ->
      Availability(UNSET_ID, AvailabilityStatus.MAYBE, timeSlot, date)
    }
  }

  /** @return current AvailabilityStatus for given date and timeSlot if any, else null */
  fun getAvailabilityStatus(date: LocalDate, timeSlot: TimeSlot): AvailabilityStatus? {
    return getByDate(date, timeSlot)?.status
  }

  /**
   * changes the AvailabilityStatus of the Availability for the given date and timeSlot (if found)
   * to the next AvailabilityStatus (round robin)
   *
   * @param date: date of the Availability of which to change the status
   * @param timeSlot: timeSlot of the Availability of which to change the status
   * @return the new AvailabilityStatus if successfully modified, else null
   */
  fun nextAvailabilityStatus(date: LocalDate, timeSlot: TimeSlot): AvailabilityStatus? {
    val currentAvailability = getByDate(date, timeSlot) ?: return null
    val newAvailabilityStatus = currentAvailability.status.next()
    setAvailabilityByDate(date, timeSlot, newAvailabilityStatus)
    return newAvailabilityStatus
  }
}
