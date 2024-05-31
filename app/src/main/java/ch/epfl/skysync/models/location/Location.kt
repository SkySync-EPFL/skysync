package ch.epfl.skysync.models.location

import ch.epfl.skysync.models.UNSET_ID

/**
 * Represents a Location.
 *
 * This is an immutable data class that holds the details of a location.
 *
 * @property id The ID of the location. By default, it is set to UNSET_ID.
 * @property userId The ID of the user associated with this location.
 * @property point The point representing the geographical coordinates of the location.
 */
data class Location(
    val id: String = UNSET_ID,
    val userId: String,
    val point: LocationPoint,
)
