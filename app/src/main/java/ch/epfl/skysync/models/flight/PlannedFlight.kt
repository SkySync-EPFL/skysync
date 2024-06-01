package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.calendar.TimeSlot
import java.time.LocalDate
import java.time.LocalTime

/**
 * Represents a planned flight.
 *
 * @property id The ID of the flight.
 * @property nPassengers The number of passengers on the flight.
 * @property flightType The type of the flight.
 * @property team The team assigned to the flight.
 * @property balloon The balloon used for the flight. It might not yet be defined on flight
 *   creation.
 * @property basket The basket used for the flight. It might not yet be defined on flight creation.
 * @property date The date of the flight.
 * @property timeSlot The time slot of the flight.
 * @property vehicles The list of vehicles used for the flight.
 */
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
  /**
   * Checks if the flight is ready to be confirmed.
   *
   * @return True if the flight is ready to be confirmed, false otherwise.
   */
  fun readyToBeConfirmed(): Boolean {
    return team.isComplete() &&
        nPassengers > 0 &&
        balloon != null &&
        basket != null &&
        vehicles.isNotEmpty()
  }

  /**
   * Returns a new flight with a role added to the team for each roleType in the given list.
   *
   * @param roles The list of roles to add.
   * @return The updated PlannedFlight.
   */
  fun addRoles(roles: List<RoleType>): PlannedFlight {
    return copy(team = team.addRolesFromRoleType(roles))
  }

  /**
   * Confirms the flight and returns a new ConfirmedFlight.
   *
   * @param meetupTimeTeam The meetup time for the team.
   * @param departureTimeTeam The departure time for the team.
   * @param meetupTimePassenger The meetup time for the passenger.
   * @param meetupLocationPassenger The meetup location for the passenger.
   * @param remarks The list of remarks.
   * @param color The color of the flight.
   * @return The new ConfirmedFlight.
   * @throws IllegalStateException If the flight is not ready to be confirmed.
   */
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

  /**
   * Gets the status of the flight.
   *
   * @return The status of the flight.
   */
  override fun getFlightStatus(): FlightStatus {
    return if (readyToBeConfirmed()) {
      FlightStatus.READY_FOR_CONFIRMATION
    } else {
      FlightStatus.IN_PLANNING
    }
  }

  /**
   * Returns a new flight with the given ID.
   *
   * @param id The new ID.
   * @return The updated PlannedFlight.
   */
  fun setId(id: String): PlannedFlight {
    return copy(id = id)
  }
}
