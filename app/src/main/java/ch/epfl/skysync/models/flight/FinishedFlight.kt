package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.location.LocationPoint
import ch.epfl.skysync.models.reports.Report
import java.time.LocalDate
import java.util.Date

/** Represents the flight when it is finished and the report has been submitted */
data class FinishedFlight(
    override val id: String,
    override val nPassengers: Int,
    override val team: Team,
    override val flightType: FlightType,
    override val balloon: Balloon,
    override val basket: Basket,
    override val date: LocalDate,
    override val timeSlot: TimeSlot,
    override val vehicles: List<Vehicle>,
    val color: FlightColor = FlightColor.NO_COLOR,
    val takeOffTime: Date,
    val takeOffLocation: LocationPoint,
    val landingTime: Date,
    val landingLocation: LocationPoint,
    val flightTime: Long, // time in milliseconds
    val reportId: List<Report> = emptyList()
) : Flight {

  private var flightStatus = FlightStatus.MISSING_REPORT

  override fun getFlightStatus(): FlightStatus {
    return this.flightStatus
  }

  fun reportCompleted() {
    this.flightStatus = FlightStatus.COMPLETED
  }
}
