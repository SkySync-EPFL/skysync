package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.user.User

/**
 * Represents a team of roles that belong to one flight operation.
 *
 * This is an immutable data class that holds all the roles of a team.
 *
 * @property roles The list of roles that belong to one flight operation.
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

  /** @return the number of roles in the team */
  fun size(): Int {
    return roles.size
  }

  /** @return The list of users assigned to the roles in the team */
  fun getUsers(): List<User> {
    return roles.mapNotNull { it.assignedUser }
  }

  /**
   * Checks if the user has the given role in the team.
   *
   * @param roleType The role to check.
   * @param userId The ID of the user to check.
   * @return True if the user has the given role in the team, false otherwise.
   */
  fun hasUserRole(roleType: RoleType, userId: String): Boolean {
    return roles.find { role -> role.roleType == roleType && role.assignedUser?.id == userId } !=
        null
  }

  /**
   * Returns a new team with the given roles added.
   *
   * @param rolesToAdd The roles to add.
   * @return The new team with the added roles.
   */
  fun addRoles(rolesToAdd: List<Role>): Team {
    val newRoles = rolesToAdd + roles
    return Team(newRoles)
  }

  /**
   * Returns a new team with a role added for each roleType in the given list.
   *
   * @param rolesToAdd The roleTypes to add.
   * @return The new team with the added roles.
   */
  fun addRolesFromRoleType(rolesToAdd: List<RoleType>): Team {
    if (rolesToAdd.isEmpty()) return this
    return addRoles(Role.initRoles(rolesToAdd))
  }

  /**
   * Returns the list of roles in the team, sorted by roleType name and assigned user ID.
   *
   * @return The sorted list of roles.
   */
  private fun sortedRoles(): List<Role> {
    return roles.sortedWith(compareBy({ it.roleType.name }, { it.assignedUser?.id }))
  }

  /**
   * Returns the hash code of the team, which is based on the sorted roles.
   *
   * @return The hash code of the team.
   */
  override fun hashCode(): Int {
    return sortedRoles().hashCode()
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
