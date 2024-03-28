package ch.epfl.skysync.models.user

import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.FlightCalendar
import ch.epfl.skysync.models.flight.RoleType

interface  User {
    val firstname: String
    val lastname: String
    val id: String
    val availabilities: AvailabilityCalendar
    val assignedFlights: FlightCalendar

    fun addRoleType(roleType: RoleType): User

    fun hasRoleType(roleType: RoleType): Boolean


}