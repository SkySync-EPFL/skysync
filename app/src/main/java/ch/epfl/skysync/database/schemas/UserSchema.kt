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
    val role: UserRole? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val roleTypes: List<RoleType>? = null,
    val balloonQualification: BalloonQualification? = null,
) : Schema<User> {
  override fun toModel(): User {
    return when (role!!) {
      UserRole.ADMIN ->
          Admin(
              id = id!!,
              firstname = firstname!!,
              lastname = lastname!!,
              availabilities = AvailabilityCalendar(),
              assignedFlights = FlightGroupCalendar(),
          )
      UserRole.CREW ->
          Crew(
              id = id!!,
              firstname = firstname!!,
              lastname = lastname!!,
              availabilities = AvailabilityCalendar(),
              assignedFlights = FlightGroupCalendar(),
          )
      UserRole.PILOT ->
          Pilot(
              id = id!!,
              firstname = firstname!!,
              lastname = lastname!!,
              availabilities = AvailabilityCalendar(),
              assignedFlights = FlightGroupCalendar(),
              qualification = balloonQualification!!)
    }
  }

  companion object {
    fun fromModel(model: User): UserSchema {
      val (userType, qualification) =
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
          role = userType,
          firstname = model.firstname,
          lastname = model.lastname,
          roleTypes = listOf(),
          balloonQualification = qualification)
    }
  }
}
