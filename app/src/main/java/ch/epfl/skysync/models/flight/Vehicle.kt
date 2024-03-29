package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.UNSET_ID

/**
 * represents a vehicle
 * @property name of the vehicle
 * @property id firebase id
 */
data class Vehicle(val name:String, val id: String = UNSET_ID)
