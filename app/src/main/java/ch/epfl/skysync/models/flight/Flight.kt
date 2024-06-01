package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.calendar.TimeSlot
import java.time.LocalDate

/**
 * Represents a flight.
 *
 * This is an interface that defines the common properties and methods for a flight.
 *
 * @property nPassengers The number of passengers on the flight.
 * @property team The team assigned to the flight.
 * @property flightType The type of the flight.
 * @property balloon The balloon used for the flight. It might not yet be defined on flight
 *   creation.
 * @property basket The basket used for the flight. It might not yet be defined on flight creation.
 * @property date The date of the flight.
 * @property timeSlot The time slot of the flight.
 * @property vehicles The list of vehicles used for the flight.
 * @property id The ID of the flight.
 */
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

  /**
   * Gets the status of the flight.
   *
   * @return The status of the flight.
   */
  fun getFlightStatus(): FlightStatus
}
