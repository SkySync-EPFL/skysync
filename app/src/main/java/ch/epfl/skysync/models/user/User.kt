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

  fun name(): String = "$firstname $lastname"

  fun doesMatchSearchQuery(query: String): Boolean {
    val matchingCombinations =
        listOf(
            "$firstname$lastname",
            "$firstname $lastname",
            "${firstname.first()} ${lastname.first()}",
            email)
    return matchingCombinations.any { it.contains(query, ignoreCase = true) }
  }
}
