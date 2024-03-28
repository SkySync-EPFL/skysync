package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.user.User

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

