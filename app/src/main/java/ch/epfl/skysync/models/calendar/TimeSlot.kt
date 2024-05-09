package ch.epfl.skysync.models.calendar

import java.time.LocalTime

/** Represent the day in two time slots, morning (AM) and afternoon (PM) */
enum class TimeSlot {
  AM,
  PM
}

/**
 * Get the time slot of a given time
 *
 * @param localTime the time to get the time slot from
 * @return AM if the time is between midnight and noon, PM otherwise
 */
fun getTimeSlot(localTime: LocalTime): TimeSlot =
    if (localTime.isBefore(LocalTime.NOON) && localTime.isAfter(LocalTime.MIDNIGHT)) {
      TimeSlot.AM
    } else {
      TimeSlot.PM
    }
