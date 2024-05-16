package ch.epfl.skysync.models.user

import ch.epfl.skysync.models.flight.RoleType

interface User {
  val id: String
  val firstname: String
  val lastname: String
  val email: String
  val roleTypes: Set<RoleType>

  fun addRoleType(roleType: RoleType): User

  fun canAssumeRole(roleType: RoleType): Boolean {
    return roleTypes.contains(roleType)
  }

  fun name(): String = "$firstname $lastname"
}
