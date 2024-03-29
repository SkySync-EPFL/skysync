package ch.epfl.skysync.models.user

import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.flight.RoleType

interface User {
  val firstname: String
  val lastname: String
  val id: String
  val availabilities: AvailabilityCalendar
  val assignedFlights: FlightGroupCalendar

  fun addRoleType(roleType: RoleType): User

  fun canAssumeRole(roleType: RoleType): Boolean
}
