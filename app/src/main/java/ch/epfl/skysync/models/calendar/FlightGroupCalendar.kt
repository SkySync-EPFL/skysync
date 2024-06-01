package ch.epfl.skysync.models.calendar

import ch.epfl.skysync.models.flight.Flight
import java.time.LocalDate

/**
 * Represents a calendar for a group of flights per slot (admin view).
 *
 * This class extends the CalendarModel with FlightGroup cells. It provides methods to add flights
 * by date and time slot, get the first flight by date and time slot, and get the FlightGroup by
 * date and time slot.
 *
 * @property cells The list of FlightGroups in the calendar.
 */
class FlightGroupCalendar(cells: List<FlightGroup> = listOf()) : CalendarModel<FlightGroup>(cells) {

  companion object {
    /**
     * Transforms the list of flights into a list of FlightGroups and returns a calendar with the
     * FlightGroup.
     *
     * @param flightList The list of flights to be transformed.
     * @return A FlightGroupCalendar with the transformed FlightGroups.
     */
    fun fromFlightList(flightList: List<Flight>): FlightGroupCalendar {
      val map = mutableMapOf<Pair<LocalDate, TimeSlot>, FlightGroup>()
      for (flight in flightList) {
        val key = Pair(flight.date, flight.timeSlot)
        if (map.containsKey(key)) {
          map[key]?.let { map[key] = it.addFlight(flight) }
        } else {
          map[key] = FlightGroup(flight.date, flight.timeSlot, listOf(flight))
        }
      }
      return FlightGroupCalendar(map.values.toList())
    }
  }

  /**
   * Sets the FlightGroup for a given date and time slot.
   *
   * @param date The date of the FlightGroup.
   * @param timeSlot The time slot of the FlightGroup.
   * @param flightGroup The FlightGroup to be set.
   * @return The updated FlightGroupCalendar.
   */
  private fun setFlightGroupByDate(
      date: LocalDate,
      timeSlot: TimeSlot,
      flightGroup: FlightGroup
  ): FlightGroupCalendar =
      setByDate(date, timeSlot) { d, t, old -> flightGroup } as FlightGroupCalendar

  /**
   * Adds the given flight to the existing FlightGroup at slot for (date, timeSlot) or adds a new
   * FlightGroup if it does not exist.
   *
   * @param date The date coordinate of the slot to add the flight to.
   * @param timeSlot The timeSlot coordinate of the slot to add the flight to.
   * @param flight The flight to add.
   * @return The updated FlightGroupCalendar.
   */
  fun addFlightByDate(date: LocalDate, timeSlot: TimeSlot, flight: Flight): FlightGroupCalendar {
    val currentFlightGroup = getByDate(date, timeSlot)
    return if (currentFlightGroup == null) {
      setFlightGroupByDate(date, timeSlot, FlightGroup(date, timeSlot, listOf(flight)))
    } else {
      setFlightGroupByDate(date, timeSlot, currentFlightGroup.addFlight(flight))
    }
  }

  /**
   * @param date date calendar slot coordinate
   * @param timeSlot timeSlot calendar slot coordinate
   * @return first flight of the FlightGroup for the given slot coordinates
   */
  fun getFirstFlightByDate(date: LocalDate, timeSlot: TimeSlot): Flight? {
    val flightGroup = getByDate(date, timeSlot) ?: return null
    return flightGroup.firstFlight()
  }

  /**
   * Returns the first flight of the FlightGroup for the given slot coordinates.
   *
   * @param date The date calendar slot coordinate.
   * @param timeSlot The timeSlot calendar slot coordinate.
   * @return The first flight of the FlightGroup for the given slot coordinates, or null if none
   *   exists.
   */
  fun getFlightGroupByDate(date: LocalDate, timeSlot: TimeSlot): FlightGroup? {
    return getByDate(date, timeSlot)
  }

  /**
   * Returns a new FlightGroupCalendar with the given cells.
   *
   * @param cells The cells to be added.
   * @return The new FlightGroupCalendar.
   */
  override fun constructor(cells: List<FlightGroup>): CalendarModel<FlightGroup> {
    return FlightGroupCalendar(cells)
  }

  /**
   * Construct a new FlightGroupCalendar with the given cells added.
   *
   * @param elementsToAdd The cells to add.
   * @return The new FlightGroupCalendar.
   */
  override fun addCells(elementsToAdd: List<FlightGroup>): FlightGroupCalendar {
    return super.addCells(elementsToAdd) as FlightGroupCalendar
  }
}
