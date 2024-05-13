package ch.epfl.skysync.models.calendar

import ch.epfl.skysync.models.flight.Flight
import java.time.LocalDate

/** calendar for a group of flights per slot (admin view) */
class FlightGroupCalendar(cells: List<FlightGroup> = listOf()) : CalendarModel<FlightGroup>(cells) {

  companion object {
    /**
     * transforms the list of flights into a list of FlightGroups and returns a calendar
     * with the FlightGroup
     */
    fun fromFlightList(flightList: List<Flight>): FlightGroupCalendar {
      val map = mutableMapOf<Pair<LocalDate, TimeSlot>, FlightGroup>()
      for (flight in flightList) {
        val key = Pair(flight.date, flight.timeSlot)
        if (map.containsKey(key)) {
          map[key] = map[key]!!.addFlight(flight)
        } else {
          map[key] = FlightGroup(flight.date, flight.timeSlot, listOf(flight))
        }
      }
      return FlightGroupCalendar(map.values.toList())
    }
  }

  private fun setFlightGroupByDate(date: LocalDate, timeSlot: TimeSlot, flightGroup: FlightGroup) {
    setByDate(date, timeSlot) { d, t, old -> flightGroup }
  }



  /**
   * adds the given flight to the existing FlightGroup at slot for (date, timeSlot) or adds a new
   * FlightGroup if non exists
   *
   * @param date the date coordinate of the slot to add the flight to
   * @param timeSlot the timeSlot coordinate of the slot to add the flight to
   * @param flight the flight to add
   */
  fun addFlightByDate(date: LocalDate, timeSlot: TimeSlot, flight: Flight) {
    val currentFlightGroup = getByDate(date, timeSlot)
    if (currentFlightGroup == null) {
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
   * @param date date calendar slot coordinate
   * @param timeSlot timeSlot calendar slot coordinate
   * @return the FlightGroup for the given slot coordinates or null if none exists
   */
  fun getFlightGroupByDate(date: LocalDate, timeSlot: TimeSlot): FlightGroup? {
    return getByDate(date, timeSlot)
  }

  override fun constructor(cells: List<FlightGroup>): CalendarModel<FlightGroup> {
    return FlightGroupCalendar(cells)
  }
}
