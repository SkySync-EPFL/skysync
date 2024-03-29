package ch.epfl.skysync.models.user

import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.flight.RoleType

class Admin(
    override val firstname: String,
    override val lastname: String,
    override val id: String,
    override val availabilities: AvailabilityCalendar,
    override val assignedFlights: FlightGroupCalendar,

    ) : User {
    private val roleTypes: Set<RoleType> = setOf()

    override fun addRoleType(roleType: RoleType): Admin {
        throw NotImplementedError()
    }

    override fun canAssumeRole(roleType: RoleType): Boolean {
        return roleTypes.contains(roleType)
    }



}
