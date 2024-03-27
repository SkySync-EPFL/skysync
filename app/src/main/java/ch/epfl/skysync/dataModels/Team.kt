package ch.epfl.skysync.dataModels

class Team {
    val roles = mutableListOf<Role>()


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


}