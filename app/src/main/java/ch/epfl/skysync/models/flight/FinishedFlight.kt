package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.location.FlightTrace
import ch.epfl.skysync.models.location.LocationPoint
import ch.epfl.skysync.models.reports.Report
import ch.epfl.skysync.models.user.Admin
import ch.epfl.skysync.models.user.User
import java.time.LocalDate
import java.util.Date

/**
 * Represents a finished flight with a submitted report.
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
 * @property color The color assigned to the flight.
 * @property takeOffTime The take off time of the flight.
 * @property takeOffLocation The take off location of the flight.
 * @property landingTime The landing time of the flight.
 * @property landingLocation The landing location of the flight.
 * @property flightTime The flight time in milliseconds.
 * @property reportId The list of report IDs.
 * @property flightTrace The flight trace.
 * @property thisFlightStatus The status of the flight.
 */
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
    val reportId: List<Report> = emptyList(),
    val flightTrace: FlightTrace = FlightTrace(UNSET_ID, emptyList()),
    private val thisFlightStatus: FlightStatus = FlightStatus.MISSING_REPORT,
) : Flight {

  override fun getFlightStatus(): FlightStatus {
    return thisFlightStatus
  }

  /**
   * Updates the flight status based on the reports submitted. For an ADMIN, a flight is considered
   * completed if all the team members have submitted their report. For a CREW/PILOT, the flight is
   * considered completed if the user has submitted their own report.
   *
   * @param user The user to check the report status for.
   * @return The updated FinishedFlight.
   */
  fun updateFlightStatus(user: User): FinishedFlight {
    return if (user is Admin) {
      updateFlightStatusForAdmin()
    } else {
      updateFlightStatusForCrewPilot(user)
    }
  }

  /**
   * Updates the flight status based on the flightIsCompleted condition.
   *
   * @param flightIsCompleted The condition to check for flight completion.
   * @return The updated FinishedFlight.
   */
  private fun updateFlightStatusForWithCondition(flightIsCompleted: () -> Boolean): FinishedFlight {
    if (flightIsCompleted()) {
      if (thisFlightStatus == FlightStatus.MISSING_REPORT) {
        return copy(thisFlightStatus = FlightStatus.COMPLETED)
      }
    } else {
      if (thisFlightStatus == FlightStatus.COMPLETED) {
        return copy(thisFlightStatus = FlightStatus.MISSING_REPORT)
      }
    }
    return this
  }

  /**
   * For a CREW/PILOT, the flight is considered completed if the user has submitted their own
   * report.
   *
   * @param user The user to check the report status for.
   * @return The updated FinishedFlight.
   */
  private fun updateFlightStatusForCrewPilot(user: User): FinishedFlight {
    return updateFlightStatusForWithCondition { reportId.any { it.authoredBy(user) } }
  }

  /**
   * For an ADMIN, a flight is considered completed if all the team members have submitted their
   * report.
   *
   * @return The updated FinishedFlight.
   */
  private fun updateFlightStatusForAdmin(): FinishedFlight {
    return updateFlightStatusForWithCondition {
      val allReportsCompleted = reportId.size == team.size()
      allReportsCompleted
    }
  }
}
