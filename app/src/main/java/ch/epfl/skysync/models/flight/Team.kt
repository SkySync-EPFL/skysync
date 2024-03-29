package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.user.User


data class Team (val roles:List<Role>)


{
    /**
     * checks if every role has a user assigned to it.
     * @return True if every role has a user assigned to it.
     */
    fun isComplete(): Boolean {
        return roles.all { it.isAssigned()}
    }


    fun assign(user: User, roleType: RoleType): Team {
        if (!user.canAssumeRole(roleType)) throw IllegalArgumentException("user can not assume role type")
        //Todo: to be implemented
        throw NotImplementedError()

    }

    fun hasNoRoles(): Boolean {
        return roles.isEmpty()
    }

    fun addRoles(rolesToAdd: List<Role>): Team{
        val newRoles =  rolesToAdd + roles
        return Team(newRoles)
    }


}