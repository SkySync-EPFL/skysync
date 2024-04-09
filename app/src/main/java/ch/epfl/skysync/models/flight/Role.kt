package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.user.User

/**
 * represents a role as part of a flight that needs to be assumed by some user
 *
 * @property roleType the type of this role
 * @property assignedUser the user that will assume this role
 * @property confirmedAttendance one flight is confirmed keeps track if the assuming user has
 *   confirmed his presence
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
}