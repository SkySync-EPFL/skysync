package ch.epfl.skysync.dataModels.userModels

import ch.epfl.skysync.dataModels.calendarModels.AvailabilityCalendar
import ch.epfl.skysync.dataModels.calendarModels.FlightCalendar
import ch.epfl.skysync.dataModels.flightModels.RoleType

interface  User {
    val firstname: String
    val lastname: String
    val userId: String
    val availabilities: AvailabilityCalendar
    val assignedFlights: FlightCalendar

    fun addRoleType(roleType: RoleType): User

    fun hasRoleType(roleType: RoleType): Boolean


}