package ch.epfl.skysync.models.user

import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.FlightCalendar
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.RoleType

class Pilot(
    override val firstname: String,
    override val lastname: String,
    override val id: String,
    override val availabilities: AvailabilityCalendar,
    override val assignedFlights: FlightCalendar,
    val qualification: BalloonQualification,

    ) : User {
    private val roleTypes: Set<RoleType> = setOf(RoleType.CREW, RoleType.PILOT)

    override fun addRoleType(roleType: RoleType): Pilot {
        TODO("Not yet implemented")
    }

    override fun hasRoleType(roleType: RoleType): Boolean {
        return roleTypes.contains(roleType)
    }
}


//val examplePilot = Pilot("John", "Doe", "1234", BalloonQualification.LARGE)