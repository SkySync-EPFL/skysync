package ch.epfl.skysync.models.calendar

import ch.epfl.skysync.models.flight.Flight
import com.google.common.collect.ImmutableList
import java.time.LocalDate


/**
 * group of flights per day
 */
data class FlightGroup(
    override val date: LocalDate,
    override val timeSlot: TimeSlot,
    val flights: ImmutableList<Flight>,
    ) : CalendarViewable {

}