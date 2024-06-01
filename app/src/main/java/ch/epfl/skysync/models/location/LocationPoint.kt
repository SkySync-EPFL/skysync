package ch.epfl.skysync.models.location

import com.google.android.gms.maps.model.LatLng
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Represents a location point.
 *
 * @property time The time elapsed since start of flight in seconds.
 * @property latitude The latitude of the location point.
 * @property longitude The longitude of the location point.
 * @property name The name of the location point.
 */
data class LocationPoint(
    val time: Int,
    val latitude: Double,
    val longitude: Double,
    val name: String = ""
) {
  /**
   * Converts this location point to a LatLng object.
   *
   * @return The LatLng object representing this location point.
   */
  fun latlng(): LatLng = LatLng(latitude, longitude)

  /**
   * Computes the distance from this location point to another location point.
   *
   * @param other The other location point.
   * @return The distance between this location point and the other location point.
   */
  fun distanceTo(other: LocationPoint): Double {
    return haversine(latitude, longitude, other.latitude, other.longitude)
  }

  companion object {
    val UNKNONWN_POINT = LocationPoint(-1, 0.0, 0.0)
  }
}

/**
 * computes the distance between two points on the earth's surface using the haversine formula based
 * on:
 * https://www.geeksforgeeks.org/haversine-formula-to-find-distance-between-two-points-on-a-sphere/
 *
 * @param lat1 The latitude of the first point.
 * @param lon1 The longitude of the first point.
 * @param lat2 The latitude of the second point.
 * @param lon2 The longitude of the second point.
 * @return The distance between the two points.
 */
fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {

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
