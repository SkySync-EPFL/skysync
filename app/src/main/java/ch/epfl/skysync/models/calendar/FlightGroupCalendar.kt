package ch.epfl.skysync.models.calendar

import ch.epfl.skysync.models.flight.Flight
import java.time.LocalDate

/** calendar for a group of flights per slot (admin view) */
class FlightGroupCalendar : CalendarModel<FlightGroup>() {
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
   * inits the calendar with empty FlightGroups for the given range
   *
   * @param from start date of the range
   * @param to end date of the range (inclusive)
   */
  override fun initForRange(from: LocalDate, to: LocalDate) {
    initForRangeSuper(from, to) { date, timeSlot -> FlightGroup(date, timeSlot, listOf()) }
  }

  /**
   * @param date date calendar slot coordinate
   * @param timeSlot timeSlot calendar slot coordinate
   * @return first flight of the FlightGroup for the given slot coordinates
   */
  fun getFlightByDate(date: LocalDate, timeSlot: TimeSlot): Flight? {
    val flightGroup = getByDate(date, timeSlot) ?: return null
    return flightGroup.firstFlight()
  }
}
