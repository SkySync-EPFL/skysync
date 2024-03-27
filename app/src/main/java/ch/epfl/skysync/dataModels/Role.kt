package ch.epfl.skysync.dataModels

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

