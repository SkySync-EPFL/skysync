package ch.epfl.skysync.models.user

import ch.epfl.skysync.models.flight.RoleType

/**
 * Represents a user
 *
 * @property id The ID of the user
 * @property firstname The first name of the user
 * @property lastname The last name of the user
 * @property email The email of the user
 * @property roleTypes The possible roles of the user
 */
interface User {
  val id: String
  val firstname: String
  val lastname: String
  val email: String
  val roleTypes: Set<RoleType>

  /**
   * Adds a role type to the user
   *
   * @param roleType The role type to add
   * @return A new user with the added role type
   */
  fun addRoleType(roleType: RoleType): User

  /**
   * Checks if the user can assume a role
   *
   * @param roleType The role type to check
   * @return True if the user can assume the role, false otherwise
   */
  fun canAssumeRole(roleType: RoleType): Boolean {
    return roleTypes.contains(roleType)
  }

  /** @return The full name of the user */
  fun name(): String = "$firstname $lastname"

  /** displays the name of a particular User class */
  fun displayRoleName(): String
}
