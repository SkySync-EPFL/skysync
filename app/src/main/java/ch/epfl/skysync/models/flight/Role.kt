package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.user.User

/**
 * represents a role as part of a flight that needs to be assumed by some user
 *
 * @property roleType the type of this role
 * @property assignedUser the user that will assume this role
 *
 * (immutable class)
 */
data class Role(
    val roleType: RoleType,
    var assignedUser: User? = null,
) {
  /**
   * assigns a given user to this role
   *
   * @param user: the user to assign to this role
   */
  fun assign(user: User): Role {
    return Role(roleType, user)
  }

  /** return true if this role was assumed by some user */
  fun isAssigned(): Boolean {
    return assignedUser != null
  }

  /**
   * @param roleType the RoleType to compare with this role's RoleType
   * @return true if this role has the given role type
   */
  fun isOfRoleType(roleType: RoleType): Boolean {
    return roleType == this.roleType
  }

  companion object {
    /**
     * produces a list of Role from a list of RoleTypes. If a RoleType R appears X times, the
     * returned list will contain X distinct roles of RoleType R
     *
     * @param roleList the list of RoleTypes for which to create roles
     * @return list of initialised roles (one role for each role type)
     */
    fun initRoles(roleList: List<RoleType>): List<Role> {
      return roleList.map { Role(it) }
    }
  }

  /**
   * Checks if this team is equal to the given object.
   *
   * @param other The object to compare with.
   * @return True if the given object is a team with the same roles, false otherwise.
   */
  override fun equals(other: Any?): Boolean {
    if (other == null) return false
    if (other::class != this::class) return false
    val otherRole = other as Role
    return (roleType == otherRole.roleType && assignedUser?.id == otherRole.assignedUser?.id)
  }

  /**
   * Checks if this team is equal to the given object.
   *
   * @param other The object to compare with.
   * @return True if the given object is a team with the same roles, false otherwise.
   */
  override fun hashCode(): Int {
    var result = roleType.hashCode()
    result = 31 * result + (assignedUser?.hashCode() ?: 0)
    return result
  }
}
