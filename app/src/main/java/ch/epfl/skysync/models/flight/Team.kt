package ch.epfl.skysync.models.flight


data class Team (val roles:List<Role>)


{
    /**
     * checks if every role has a user assigned to it.
     * @return True if every role has a user assigned to it.
     */
    fun isComplete(): Boolean {
        return roles.all { it.isAssigned()}
    }

    fun hasNoRoles(): Boolean {
        return roles.isEmpty()
    }

    fun addRoles(rolesToAdd: List<Role>): Team{
        val newRoles =  rolesToAdd + roles
        return Team(newRoles)
    }


}