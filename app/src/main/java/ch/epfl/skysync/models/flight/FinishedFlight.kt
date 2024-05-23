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
    val reportId: List<Report> = emptyList(),
    val flightTrace: FlightTrace = FlightTrace(UNSET_ID, emptyList()),
    private val thisFlightStatus: FlightStatus = FlightStatus.MISSING_REPORT,
) : Flight {

  override fun getFlightStatus(): FlightStatus {
    return thisFlightStatus
  }

    /**
     * Update the flight status based on the reports submitted
     * For an ADMIN a flight is considered completed iff all the team members have submitted
     * their report.
     * For an CREW/PILOT the flight is considered completed iff the user has submitted their own report
     *
     */
    fun updateFlightStatus(user: User): FinishedFlight {
        return if (user is Admin) {
            updateFlightStatusForAdmin()
        } else{
            updateFlightStatusForCrewPilot(user)
        }
    }

    /**
     * Update the flight status based on the flightIsCompleted condition
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
     * For an CREW/PILOT the flight is considered completed iff the user has submitted their own report
     */
    private fun updateFlightStatusForCrewPilot(user: User): FinishedFlight {
        return updateFlightStatusForWithCondition { reportId.any {it.authoredBy(user)} }
    }

    /**
     * For an ADMIN a flight is considered completed iff all the team members have submitted
     * their report.
     */
    private fun updateFlightStatusForAdmin(): FinishedFlight {
        return updateFlightStatusForWithCondition { reportId.size == team.size() }
    }
}
