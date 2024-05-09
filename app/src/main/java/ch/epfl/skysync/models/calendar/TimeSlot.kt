package ch.epfl.skysync.models.calendar

import java.time.LocalTime

/** Represent the day in two time slots, morning (AM) and afternoon (PM) */
enum class TimeSlot {
  AM,
  PM
}

fun getCurrentTimeSlot() {
  val currentTime = LocalTime.now()
}


