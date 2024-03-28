package ch.epfl.skysync.dataModels.calendarModels

import java.time.LocalDate

interface CalendarViewable {
    val date: LocalDate
    val timeSlot: TimeSlot
}