package ch.epfl.skysync.dataModels.userModels

import ch.epfl.skysync.dataModels.calendarModels.AvailabilityCalendar
import ch.epfl.skysync.dataModels.calendarModels.FlightCalendar
import ch.epfl.skysync.dataModels.flightModels.BalloonQualification
import ch.epfl.skysync.dataModels.flightModels.RoleType

class Pilot(
    override val firstname: String,
    override val lastname: String,
    override val userId: String,
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