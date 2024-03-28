package ch.epfl.skysync.dataModels.userModels

import ch.epfl.skysync.dataModels.calendarModels.AvailabilityCalendar
import ch.epfl.skysync.dataModels.calendarModels.FlightCalendar
import ch.epfl.skysync.dataModels.flightModels.RoleType

class Crew(
    override val firstname: String,
    override val lastname: String,
    override val userId: String,
    override val availabilities: AvailabilityCalendar,
    override val assignedFlights: FlightCalendar,

) : User {

    private val roleTypes: Set<RoleType> = setOf(RoleType.CREW)

    override fun addRoleType(roleType: RoleType): Crew {
        TODO("Not yet implemented")
    }

    override fun hasRoleType(roleType: RoleType): Boolean {
        return roleTypes.contains(roleType)
    }



}
