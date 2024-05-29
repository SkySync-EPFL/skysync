package ch.epfl.skysync.models.user

import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.flight.RoleType

data class Admin(
    override val id: String = UNSET_ID,
    override val firstname: String,
    override val lastname: String,
    override val email: String,
    override val roleTypes: Set<RoleType> = setOf(RoleType.ADMIN),
) : User {
  override fun addRoleType(roleType: RoleType): Admin {
    return this.copy(roleTypes = roleTypes + roleType)
  }
}
