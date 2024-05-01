package ch.epfl.skysync.models.user

import ch.epfl.skysync.database.UserRole
import ch.epfl.skysync.database.schemas.UserSchema
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.flight.BalloonQualification

class TempUser(
    val email: String = UNSET_ID,
    val userRole: UserRole,
    val firstname: String,
    val lastname: String,
    val balloonQualification: BalloonQualification? = null,
) {
  fun toUserSchema(uid: String): UserSchema {
    return UserSchema(
        id = uid,
        userRole = userRole,
        firstname = firstname,
        lastname = lastname,
        email = email,
        roleTypes = listOf(),
        balloonQualification = balloonQualification,
    )
  }
}
