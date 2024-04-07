package ch.epfl.skysync.models.user

import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.RoleType

class Pilot(
    override val id: String = UNSET_ID,
    override val firstname: String,
    override val lastname: String,
    override val availabilities: AvailabilityCalendar,
    override val assignedFlights: FlightGroupCalendar,
    val qualification: BalloonQualification,
) : User {
  private val roleTypes: Set<RoleType> = setOf(RoleType.CREW, RoleType.PILOT)

  override fun addRoleType(roleType: RoleType): Pilot {
    throw NotImplementedError()
  }

  override fun canAssumeRole(roleType: RoleType): Boolean {
    return roleTypes.contains(roleType)
  }
}

// val examplePilot = Pilot("John", "Doe", "1234", BalloonQualification.LARGE)
