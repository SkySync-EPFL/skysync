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

  /**
   * @return the number of roles in the team
   */
  fun size(): Int {
    return roles.size
  }

  fun getUsers(): List<User> {
    return roles.mapNotNull { it.assignedUser }
  }

  /** Returns if the user has the given role in the team */
  fun hasUserRole(roleType: RoleType, userId: String): Boolean {
    return roles.find { role -> role.roleType == roleType && role.assignedUser?.id == userId } !=
        null
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
    return roles.sortedWith(compareBy({ it.roleType.name }, { it.assignedUser?.id }))
  }

  override fun hashCode(): Int {
    return sortedRoles().hashCode()
  }

  override fun equals(other: Any?): Boolean {
    if (other == null) return false
    if (other::class != this::class) return false
    val otherTeam = other as Team

    val otherTeamSortedRoles = otherTeam.sortedRoles()
    val thisTeamSortedRoles = this.sortedRoles()
    if (otherTeamSortedRoles.size != thisTeamSortedRoles.size) return false
    val haveSameRoles =
        otherTeamSortedRoles.zip(thisTeamSortedRoles).all { (otherRole, thisRole) ->
          otherRole == thisRole
        }
    return haveSameRoles
  }
}
