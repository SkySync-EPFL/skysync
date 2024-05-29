package ch.epfl.skysync.models.location

import com.google.android.gms.maps.model.LatLng
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/** @param time The time elapsed since start of flight in seconds */
data class LocationPoint(
    val time: Int,
    val latitude: Double,
    val longitude: Double,
    val name: String = ""
) {
    fun latlng(): LatLng = LatLng(latitude, longitude)
    fun distanceTo(other: LocationPoint): Double {
        return haversine(latitude, longitude, other.latitude, other.longitude)
    }

    companion object {
        val UNKNONWN_POINT = LocationPoint(-1, 0.0, 0.0)

    }
}


/**
 * computes the distance between two points on the earth's surface using the haversine formula
 */
fun haversine(
    lat1: Double, lon1: Double,
    lat2: Double, lon2: Double
): Double {


    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)


    // convert to radians
    val lat1Rad = Math.toRadians(lat1)
    val lat2Rad = Math.toRadians(lat2)

    // apply formulae
    val a: Double = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(lat1Rad) * cos(lat2Rad)
    val rad = 6371.0
    val c = 2 * asin(sqrt(a))
    return rad * c
}
