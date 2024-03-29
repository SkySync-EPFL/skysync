package ch.epfl.skysync.models.calendar

import ch.epfl.skysync.models.flight.Flight
import java.time.LocalDate

/** group of flights per date and timeSlot */
data class FlightGroup(
    override val date: LocalDate,
    override val timeSlot: TimeSlot,
    val flights: List<Flight>,
) : CalendarViewable {

  /**
   * adds the given flight to the group of flights
   *
   * @param flight the flight to add
   * @return a new FlightGroup with the added flight
   */
  fun addFlight(flight: Flight): FlightGroup {
    return FlightGroup(date, timeSlot, flights + flight)
  }

  fun isEmpty(): Boolean {
    return flights.isEmpty()
  }

  fun firstFlight(): Flight? {
    return if (!isEmpty()) flights[0] else null
  }
}
