package ch.epfl.skysync.models.location

import com.google.android.gms.maps.model.LatLng

/** @param time The time elapsed since start of flight in seconds */
data class LocationPoint(
    val time: Int,
    val latitude: Double,
    val longitude: Double,
    val name: String = ""
) {
  fun latlng(): LatLng = LatLng(latitude, longitude)

    companion object {
        val UNKNONWN_POINT = LocationPoint(-1, 0.0, 0.0)
    }
}


