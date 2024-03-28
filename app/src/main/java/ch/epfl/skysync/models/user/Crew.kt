package ch.epfl.skysync.models.user

import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.FlightCalendar
import ch.epfl.skysync.models.flight.RoleType

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
