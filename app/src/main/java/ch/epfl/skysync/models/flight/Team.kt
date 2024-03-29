package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.user.User

/**
 * represents group of roles that belong to one flight operation
 * @property roles the list of roles that belong to one flight operation
 * (immutable class)
 */
data class Team (val roles:List<Role>)


{
    /**
     * checks if every role has a user assigned to it.
     * @return True if at least one role and every role has a user assigned to it.
     */
    fun isComplete(): Boolean {
        return roles.isNotEmpty() && roles.all { it.isAssigned()}
    }

    /**
     * assigns the given user to the first role with the given role type
     */
    fun assign(user: User, roleType: RoleType): Team {
        //Todo: to be implemented
        throw NotImplementedError()

    }

    /**
     * @param rolesToAdd: the roles that will be added to this team
     * @return new team instance with the added roles
     */
    fun addRoles(rolesToAdd: List<Role>): Team{
        val newRoles =  rolesToAdd + roles
        return Team(newRoles)
    }


}