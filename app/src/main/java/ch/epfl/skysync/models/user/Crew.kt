package ch.epfl.skysync.models.user

import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.flight.RoleType

/**
 * Represents a crew user
 *
 * @param id The ID of the user. By default, it is set to UNSET_ID.
 * @param firstname The first name of the user
 * @param lastname The last name of the user
 * @param email The email of the user
 * @param roleTypes The roles of the user. By default, it is set to setOf(RoleType.ADMIN).
 */
data class Crew(
    override val id: String = UNSET_ID,
    override val firstname: String,
    override val lastname: String,
    override val email: String,
    override val roleTypes: Set<RoleType> = setOf(RoleType.CREW),
) : User {
  override fun addRoleType(roleType: RoleType): Crew {
    return this.copy(roleTypes = roleTypes + roleType)
  }

  override fun displayRoleName(): String = "Crew"
}
