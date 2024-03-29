package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.calendar.TimeSlot
import java.time.LocalDate

interface Flight {
  val nPassengers: Int
  val team: Team
  val flightType: FlightType
  val balloon: Balloon? // might not yet be defined on flight creation
  val basket: Basket? // might not yet be defined on flight creation
  val date: LocalDate
  val timeSlot: TimeSlot
  val vehicles: List<Vehicle>
  val id: String
}
