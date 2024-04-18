package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.calendar.TimeSlot
import java.time.LocalDate
import java.time.LocalTime

data class PlannedFlight(
    override val id: String,
    override val nPassengers: Int,
    override val flightType: FlightType,
    override val team: Team =
        Team(listOf())
            .addRolesFromRoleType(BASE_ROLES)
            .addRolesFromRoleType(flightType.specialRoles),
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

  /** returns a new flight with a role added to the team for each roleType in the given list to */
  fun addRoles(roles: List<RoleType>): PlannedFlight {
    return copy(team = team.addRolesFromRoleType(roles))
  }

  fun confirmFlight(
      meetupTimeTeam: LocalTime,
      departureTimeTeam: LocalTime,
      meetupTimePassenger: LocalTime,
      meetupLocationPassenger: String,
      remarks: List<String>,
      color: FlightColor
  ): ConfirmedFlight {
    if (!readyToBeConfirmed()) {
      throw IllegalStateException("Flight is not ready to be confirmed")
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
        meetupLocationPassenger = meetupLocationPassenger)
  }

  override fun getFlightStatus(): FlightStatus {
    return if (readyToBeConfirmed()) {
      FlightStatus.READY_FOR_CONFIRMATION
    } else {
      FlightStatus.IN_PLANNING
    }
  }

    fun setId(id: String): PlannedFlight {
        return copy(id = id)
    }
}
