package ch.epfl.skysync.models.user

import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.RoleType

data class Pilot(
    override val id: String = UNSET_ID,
    override val firstname: String,
    override val lastname: String,
    override val email: String,
    override val availabilities: AvailabilityCalendar,
    override val assignedFlights: FlightGroupCalendar,
    override val roleTypes: Set<RoleType> = setOf(RoleType.CREW, RoleType.PILOT),
    val qualification: BalloonQualification,
) : User {

  override fun addRoleType(roleType: RoleType): Pilot {
    return this.copy(roleTypes = roleTypes + roleType)
  }
}
