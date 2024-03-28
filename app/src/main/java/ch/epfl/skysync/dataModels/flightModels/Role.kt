package ch.epfl.skysync.dataModels.flightModels

import ch.epfl.skysync.dataModels.flightModels.RoleType
import ch.epfl.skysync.dataModels.userModels.User

class Role(
    val roleType: RoleType,
    var assignedUsr: User? = null,
)

{
    fun assign(user: User) {
        assignedUsr = user
    }

    fun isAssigned(): Boolean {
        return assignedUsr != null
    }
}

