package ch.epfl.skysync.models.user

import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.RoleType

/**
 * Represents a pilot user
 *
 * @param id The ID of the user. By default, it is set to UNSET_ID.
 * @param firstname The first name of the user
 * @param lastname The last name of the user
 * @param email The email of the user
 * @param roleTypes The roles of the user. By default, it is set to setOf(RoleType.PILOT).
 * @param qualification The balloon qualification of the pilot
 */
data class Pilot(
    override val id: String = UNSET_ID,
    override val firstname: String,
    override val lastname: String,
    override val email: String,
    override val roleTypes: Set<RoleType> = setOf(RoleType.CREW, RoleType.PILOT),
    val qualification: BalloonQualification,
) : User {

  /**
   * Adds a role type to the user
   *
   * @param roleType The role type to add
   * @return A new user with the added role type
   */
  override fun addRoleType(roleType: RoleType): Pilot {
    return this.copy(roleTypes = roleTypes + roleType)
  }
}
