package ch.epfl.skysync.models.location

import com.google.android.gms.maps.model.LatLng

/** @param time The time elapsed since start of flight in seconds */
data class LocationPoint(
    val time: Int,
    val latitude: Double,
    val longitude: Double,
) {
  fun latlng(): LatLng = LatLng(latitude, longitude)
}
