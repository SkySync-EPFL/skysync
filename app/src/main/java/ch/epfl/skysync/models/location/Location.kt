package ch.epfl.skysync.models.location

import ch.epfl.skysync.models.UNSET_ID

/**
 * Represents a Location
 *
 * @param userId Use userID as the location ID directly
 */
data class Location(
    val id: String = UNSET_ID,
    val userId: String,
    val point: LocationPoint,
)
