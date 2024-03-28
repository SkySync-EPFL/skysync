package ch.epfl.skysync.dataModels.calendarModels

import java.time.LocalDate

class AvailabilityCalendar: CalendarModel<Availability>() {
    fun setAvailabilityByDate(date: LocalDate, timeSlot: TimeSlot, status: AvailabilityStatus) {
        setByDate(date, timeSlot) { d, t , old -> old.setStatus(status)}
    }

    override fun initForRange(from: LocalDate, to: LocalDate) {
        initForRangeSuper(from, to) {
                                       date, timeSlot -> Availability("", AvailabilityStatus.MAYBE, timeSlot, date)
        }
    }


}