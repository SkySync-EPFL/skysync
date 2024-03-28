package ch.epfl.skysync.dataModels.calendarModels

import com.google.common.collect.ImmutableList
import java.time.LocalDate

class FlightCalendar: CalendarModel<FlightGroup>() {
    fun setFlightGroupByDate(date: LocalDate, timeSlot: TimeSlot, flightGroup: FlightGroup) {
        setByDate(date, timeSlot) { d, t , old -> flightGroup}
    }

    override fun initForRange(from: LocalDate, to: LocalDate) {
        initForRangeSuper(from, to) {
                date, timeSlot -> FlightGroup(date, timeSlot, ImmutableList.of())
        }
    }
}