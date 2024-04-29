package ch.epfl.skysync.models.location

import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.user.User
import com.google.android.gms.maps.model.LatLng

/**
 * Represents a Location
 *
 * @param user The user of whom we share the location
 * @param value His actual location
 */
data class Location(
    val id: String = UNSET_ID,
    val user: User,
    val value: LatLng,
)