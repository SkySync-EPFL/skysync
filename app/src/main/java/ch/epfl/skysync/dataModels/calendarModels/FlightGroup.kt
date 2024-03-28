package ch.epfl.skysync.dataModels.calendarModels

import ch.epfl.skysync.dataModels.flightModels.Flight
import com.google.common.collect.ImmutableList
import java.time.LocalDate

data class FlightGroup(
    override val date: LocalDate,
    override val timeSlot: TimeSlot,
    val flights: ImmutableList<Flight>,
    ) : CalendarViewable {

}