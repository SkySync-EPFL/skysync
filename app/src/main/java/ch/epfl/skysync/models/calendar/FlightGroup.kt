package ch.epfl.skysync.models.calendar

import ch.epfl.skysync.models.flight.Flight
import java.time.LocalDate

/**
 * Represents a group of flights for a specific date and time slot.
 *
 * This is an immutable data class that holds the date, time slot, and a list of flights.
 *
 * @property date The date for which this group of flights applies.
 * @property timeSlot The time slot for which this group of flights applies.
 * @property flights The list of flights in this group.
 */
data class FlightGroup(
    override val date: LocalDate,
    override val timeSlot: TimeSlot,
    val flights: List<Flight>,
) : CalendarViewable {

  /**
   * Adds a flight to the group of flights.
   *
   * This function returns a new [FlightGroup] with the added flight, preserving immutability.
   *
   * @param flight The flight to be added.
   * @return A new [FlightGroup] with the added flight.
   */
  fun addFlight(flight: Flight): FlightGroup {
    return FlightGroup(date, timeSlot, flights + flight)
  }

  /**
   * Checks if the group of flights is empty.
   *
   * @return True if the group of flights is empty, false otherwise.
   */
  fun isEmpty(): Boolean {
    return flights.isEmpty()
  }

  /**
   * Returns the first flight in the group of flights, if any.
   *
   * @return The first flight in the group of flights, or null if the group is empty.
   */
  fun firstFlight(): Flight? {
    return if (!isEmpty()) flights[0] else null
  }
}
