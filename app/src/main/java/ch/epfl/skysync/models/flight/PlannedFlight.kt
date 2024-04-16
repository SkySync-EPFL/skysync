package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.calendar.TimeSlot
import java.time.LocalDate
import java.time.LocalTime

data class PlannedFlight(
    override val id: String,
    override val nPassengers: Int,
    override val team: Team = Team(Role.initRoles(BASE_ROLES)),
    override val flightType: FlightType,
    override val balloon: Balloon?,
    override val basket: Basket?,
    override val date: LocalDate,
    override val timeSlot: TimeSlot,
    override val vehicles: List<Vehicle>
) : Flight {
  fun readyToBeConfirmed(): Boolean {
    return team.isComplete() &&
            nPassengers > 0 &&
            balloon != null &&
            basket != null &&
            vehicles.isNotEmpty()
  }

    fun confirmFlight(
        notReadyToConfirm: ()->Unit,
        meetupTimeTeam: LocalTime,
        departureTimeTeam: LocalTime,
        meetupTimePassenger: LocalTime,
        meetupLocationPassenger: String,
        remarks: List<String>,
        color: FlightColor


    ): ConfirmedFlight {
        if (!readyToBeConfirmed()) {
            notReadyToConfirm()
        }
        return ConfirmedFlight(
            id,
            nPassengers,
            team,
            flightType,
            balloon!!,
            basket!!,
            date,
            timeSlot,
            vehicles,
            remarks = remarks,
            color = color,
            meetupTimeTeam = meetupTimeTeam,
            departureTimeTeam = departureTimeTeam,
            meetupTimePassenger = meetupTimePassenger,
            meetupLocationPassenger = meetupLocationPassenger

        )
    }
}
