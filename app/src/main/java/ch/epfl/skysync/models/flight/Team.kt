package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.user.User

/**
 * represents group of roles that belong to one flight operation
 *
 * @property roles the list of roles that belong to one flight operation (immutable class)
 */
data class Team(val roles: List<Role>) {

  /**
   * checks if every role has a user assigned to it.
   *
   * @return True if at least one role and every role has a user assigned to it.
   */
  fun isComplete(): Boolean {
    return roles.isNotEmpty() && roles.all { it.isAssigned() }
  }

  fun getUsers(): List<User> {
    return roles.mapNotNull { it.assignedUser }
  }

  /** Returns if the user has the given role in the team */
  fun hasUserRole(roleType: RoleType, userId: String): Boolean {
    return roles.find { role -> role.roleType == roleType && role.assignedUser?.id == userId } !=
        null
  }

  /** assigns the given user to the first role with the given role type */
  fun assign(user: User, roleType: RoleType): Team {
    // Todo: to be implemented
    throw NotImplementedError()
  }

  /**
   * @param rolesToAdd: the roles that will be added to this team
   * @return new team instance with the added roles
   */
  fun addRoles(rolesToAdd: List<Role>): Team {
    val newRoles = rolesToAdd + roles
    return Team(newRoles)
  }

  /**
   * @param rolesToAdd: the roleTypes for which a role will be added to this team
   * @return new team instance with the added roles
   */
  fun addRolesFromRoleType(rolesToAdd: List<RoleType>): Team {
    if (rolesToAdd.isEmpty()) return this
    return addRoles(Role.initRoles(rolesToAdd))
  }

  private fun sortedRoles(): List<Role> {
    return roles.sortedBy { it.assignedUser?.id ?: it.roleType.name }
  }

  override fun hashCode(): Int {
    return sortedRoles().hashCode()
  }

  override fun equals(other: Any?): Boolean {
    if (other == null) return false
    if (other::class != this::class) return false
    // we do not take into account the order in which the roles have been added
    // when performing equality check
    return (other as Team).sortedRoles() == this.sortedRoles()
  }
}
