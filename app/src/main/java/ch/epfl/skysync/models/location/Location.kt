package ch.epfl.skysync.models.location

import ch.epfl.skysync.models.UNSET_ID
import com.google.android.gms.maps.model.LatLng

/**
 * Represents a Location
 *
 * @param id Use userID as the location ID directly
 * @param value His actual location
 */
data class Location(
    val id: String = UNSET_ID,
    val value: LatLng,
)
