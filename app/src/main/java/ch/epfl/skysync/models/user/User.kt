package ch.epfl.skysync.models.user

import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.flight.RoleType

interface User {
  val id: String
  val firstname: String
  val lastname: String
  val email: String
  val availabilities: AvailabilityCalendar
  val assignedFlights: FlightGroupCalendar
  val roleTypes: Set<RoleType>

  fun addRoleType(roleType: RoleType): User

  fun canAssumeRole(roleType: RoleType): Boolean {
    return roleTypes.contains(roleType)
  }

  fun displayString(): String {
    return "$firstname $lastname"
  }
}
