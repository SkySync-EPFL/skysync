package ch.epfl.skysync.models.calendar

import java.time.LocalDate

/**
 * calendar for a group of flights per slot (admin view)
 *
 */
class FlightGroupCalendar: CalendarModel<FlightGroup>() {
    fun setFlightGroupByDate(date: LocalDate, timeSlot: TimeSlot, flightGroup: FlightGroup) {
        throw NotImplementedError()
        //setByDate(date, timeSlot) { d, t , old -> flightGroup}
    }

    override fun initForRange(from: LocalDate, to: LocalDate) {
        throw NotImplementedError()
//        initForRangeSuper(from, to) {
//                date, timeSlot -> FlightGroup(date, timeSlot, ImmutableList.of())
//        }
    }
}