package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.Schema
import ch.epfl.skysync.database.UserRole
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.user.Admin
import ch.epfl.skysync.models.user.Crew
import ch.epfl.skysync.models.user.Pilot
import ch.epfl.skysync.models.user.User
import com.google.firebase.firestore.DocumentId
import java.lang.UnsupportedOperationException

data class UserSchema(
    @DocumentId val id: String? = null,
    val userRole: UserRole? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val email: String? = null,
    val roleTypes: List<RoleType>? = null,
    val balloonQualification: BalloonQualification? = null,
) : Schema<User> {
  override fun toModel(): User {
    return when (userRole!!) {
      UserRole.ADMIN ->
          Admin(
              id = id!!,
              firstname = firstname!!,
              lastname = lastname!!,
              email = email!!,
              roleTypes = roleTypes!!.toSet(),
          )
      UserRole.CREW ->
          Crew(
              id = id!!,
              firstname = firstname!!,
              lastname = lastname!!,
              email = email!!,
              roleTypes = roleTypes!!.toSet(),
          )
      UserRole.PILOT ->
          Pilot(
              id = id!!,
              firstname = firstname!!,
              lastname = lastname!!,
              email = email!!,
              roleTypes = roleTypes!!.toSet(),
              qualification = balloonQualification!!)
    }
  }

  companion object {
    fun fromModel(model: User): UserSchema {
      val (userRole, qualification) =
          when (model) {
            is Admin -> Pair(UserRole.ADMIN, null)
            is Crew -> Pair(UserRole.CREW, null)
            is Pilot -> Pair(UserRole.PILOT, model.qualification)
            else ->
                throw UnsupportedOperationException(
                    "Unexpected class ${model.javaClass.simpleName}")
          }
      return UserSchema(
          id = model.id,
          userRole = userRole,
          firstname = model.firstname,
          lastname = model.lastname,
          email = model.email,
          roleTypes = model.roleTypes.toList(),
          balloonQualification = qualification)
    }
  }
}
