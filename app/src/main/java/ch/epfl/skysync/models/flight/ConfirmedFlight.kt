package ch.epfl.skysync.models.flight

import ch.epfl.skysync.database.DateUtility.localDateAndTimeToDate
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.location.FlightTrace
import ch.epfl.skysync.models.location.LocationPoint
import ch.epfl.skysync.models.reports.Report
import java.time.LocalDate
import java.time.LocalTime

/**
 * Represents a confirmed flight.
 *
 * @property id The ID of the flight.
 * @property nPassengers The number of passengers on the flight.
 * @property team The team assigned to the flight.
 * @property flightType The type of the flight.
 * @property balloon The balloon used for the flight.
 * @property basket The basket used for the flight.
 * @property date The date of the flight.
 * @property timeSlot The time slot of the flight.
 * @property vehicles The list of vehicles used for the flight.
 * @property remarks The list of remarks for the flight.
 * @property color The color assigned to the flight.
 * @property meetupTimeTeam The meetup time for the team.
 * @property departureTimeTeam The departure time for the team.
 * @property meetupTimePassenger The meetup time for the passenger.
 * @property meetupLocationPassenger The meetup location for the passenger.
 * @property isOngoing Whether the flight is ongoing.
 * @property startTimestamp The start timestamp of the flight.
 */
data class ConfirmedFlight(
    override val id: String,
    override val nPassengers: Int,
    override val team: Team,
    override val flightType: FlightType,
    override val balloon: Balloon,
    override val basket: Basket,
    override val date: LocalDate,
    override val timeSlot: TimeSlot,
    override val vehicles: List<Vehicle>,
    val remarks: List<String>,
    val color: FlightColor = FlightColor.NO_COLOR,
    val meetupTimeTeam: LocalTime,
    val departureTimeTeam: LocalTime,
    val meetupTimePassenger: LocalTime,
    val meetupLocationPassenger: String,
    val isOngoing: Boolean = false,
    val startTimestamp: Long? = null,
) : Flight {
  /**
   * Returns the status of the flight.
   *
   * @return The status of the flight.
   */
  override fun getFlightStatus(): FlightStatus {
    return FlightStatus.CONFIRMED
  }

  /**
   * Creates a finished flight from a confirmed flight.
   *
   * @param takeOffTime The take off time of the flight.
   * @param takeOffLocation The take off location of the flight.
   * @param landingTime The landing time of the flight.
   * @param landingLocation The landing location of the flight.
   * @param flightTime The flight time.
   * @param reportIds The list of report IDs.
   * @param flightTrace The flight trace.
   * @return A new instance of [FinishedFlight].
   */
  fun finishFlight(
      takeOffTime: LocalTime,
      takeOffLocation: LocationPoint,
      landingTime: LocalTime,
      landingLocation: LocationPoint,
      flightTime: Long,
      reportIds: List<Report> = listOf(),
      flightTrace: FlightTrace
  ): FinishedFlight =
      FinishedFlight(
          id = id,
          nPassengers = nPassengers,
          team = team,
          flightType = flightType,
          balloon = balloon,
          basket = basket,
          date = date,
          timeSlot = timeSlot,
          vehicles = vehicles,
          color = color,
          landingTime = localDateAndTimeToDate(date, landingTime),
          landingLocation = landingLocation,
          takeOffTime = localDateAndTimeToDate(date, takeOffTime),
          takeOffLocation = takeOffLocation,
          flightTime = flightTime,
          flightTrace = flightTrace,
          reportId = reportIds)
}
