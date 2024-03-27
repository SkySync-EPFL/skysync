package ch.epfl.skysync.dataModels

import java.time.LocalDate

class AvailabilityCalendar: CalendarModel<Availability>() {
    fun setAvailabilityByDate(date: LocalDate, timeSlot: TimeSlot, status: AvailabilityStatus) {
        setByDate(date, timeSlot) { d, t , old -> old.setStatus(status)}
    }

    fun getAvailabilityByDate(date: LocalDate, timeSlot: TimeSlot): Availability? {
        return getByDate(date, timeSlot)
    }
}