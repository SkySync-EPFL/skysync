package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.calendar.CalendarViewable
import ch.epfl.skysync.models.calendar.TimeSlot
import java.time.LocalDate

interface Flight: CalendarViewable{
    val id: Int
    val nPassengers: Int
    val team: Team
    val flightType: FlightType
    val balloon: Balloon? // might not yet be defined on flight creation
    val basket: Basket? // might not yet be defined on flight creation
    override val date: LocalDate
    override val timeSlot: TimeSlot
    val vehicles: List<Vehicle>

}