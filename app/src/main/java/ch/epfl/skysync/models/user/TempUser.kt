package ch.epfl.skysync.models.user

import ch.epfl.skysync.database.UserRole
import ch.epfl.skysync.database.schemas.UserSchema
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.flight.BalloonQualification

/**
 * Represents a temporary user.
 *
 * @param email The email of the user.
 * @param userRole The role of the user.
 * @param firstname The first name of the user.
 * @param lastname The last name of the user.
 * @param balloonQualification The balloon qualification of the user. By default, it is set to null.
 */
class TempUser(
    val email: String = UNSET_ID,
    val userRole: UserRole,
    val firstname: String,
    val lastname: String,
    val balloonQualification: BalloonQualification? = null,
) {
  /**
   * Converts the temporary user to a user schema.
   *
   * @param uid The ID of the user.
   * @return The user schema.
   */
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
