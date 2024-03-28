package ch.epfl.skysync.models.calendar

import java.time.LocalDate

interface CalendarViewable {
    val date: LocalDate
    val timeSlot: TimeSlot
}