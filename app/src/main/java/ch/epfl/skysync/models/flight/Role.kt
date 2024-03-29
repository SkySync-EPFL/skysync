package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.user.User

/**
 * represents a role as part of a flight that needs to be assumed by some user
 * @property roleType the type of this role
 * @property assignedUser the user that will assume this role
 * @property confirmedAttendance one flight is confirmed keeps track if the assuming user
 * has confirmed his presence
 *
 * (immutable class)
 */
data class Role(
    val roleType: RoleType,
    var assignedUser: User? = null,
)

{
    /**
     * assigns a given user to this role
     * @param user: the user to assign to this role
     */
    fun assign(user: User): Role {
        return Role(roleType, user)
    }

    /**
     * return true if this role was assumed by some user
     */
    fun isAssigned(): Boolean {
        return assignedUser != null
    }

    fun isOfRoleType(roleType: RoleType): Boolean{
        return roleType == this.roleType
    }

    companion object{
        fun initRoles(roleList: List<RoleType>): List<Role> {
            return roleList.map{Role(it)}

        }

    }

}

