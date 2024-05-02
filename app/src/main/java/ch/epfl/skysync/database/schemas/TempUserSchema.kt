package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.Schema
import ch.epfl.skysync.database.UserRole
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.user.TempUser
import com.google.firebase.firestore.DocumentId

data class TempUserSchema(
    @DocumentId val email: String? = null,
    val userRole: UserRole? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val balloonQualification: BalloonQualification? = null
) : Schema<TempUser> {
  override fun toModel(): TempUser {
    return TempUser(
        email = email!!,
        userRole = userRole!!,
        firstname = firstname!!,
        lastname = lastname!!,
        balloonQualification = balloonQualification)
  }

  companion object {
    fun fromModel(model: TempUser): TempUserSchema {
      return TempUserSchema(
          email = model.email,
          userRole = model.userRole,
          firstname = model.firstname,
          lastname = model.lastname,
          balloonQualification = model.balloonQualification)
    }
  }
}
