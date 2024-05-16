package ch.epfl.skysync.models.flight

import ch.epfl.skysync.database.DateUtility.localDateAndTimeToDate
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.location.FlightTrace
import ch.epfl.skysync.models.location.LocationPoint
import ch.epfl.skysync.models.reports.Report
import java.time.LocalDate
import java.time.LocalTime

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
  override fun getFlightStatus(): FlightStatus {
    return FlightStatus.CONFIRMED
  }

    /**
     * create a finished Flight from a confirmed flight
     */
    fun finishFlight(
        takeOffTime: LocalTime,
        takeOffLocation: LocationPoint,
        landingTime: LocalTime,
        landingLocation: LocationPoint,
        flightTime: Long,
        reportIds: List<Report> = listOf(),
        flightTrace: FlightTrace
    ): FinishedFlight  =
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
            reportId = reportIds
        )
}
