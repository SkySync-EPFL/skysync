package ch.epfl.skysync.models.calendar

import java.time.LocalDate

/** defines a slot of a calendar that must be uniquely identified by its date and timeSlot */
interface CalendarViewable {
  val date: LocalDate
  val timeSlot: TimeSlot
}
