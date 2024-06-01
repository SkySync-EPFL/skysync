package ch.epfl.skysync.models.location

/**
 * Represents the user's metrics.
 *
 * @property speed The speed of the user.
 * @property altitude The altitude of the user.
 * @property bearing The bearing of the user.
 * @property verticalSpeed The vertical speed of the user.
 * @property location The location point of the user.
 */
data class UserMetrics(
    val speed: Float,
    val altitude: Double,
    val bearing: Float,
    val verticalSpeed: Double,
    val location: LocationPoint,
) {
  /**
   * Returns a new UserMetrics with updated values.
   *
   * @param speed The new speed of the user.
   * @param newAltitude The new altitude of the user.
   * @param bearing The new bearing of the user.
   * @param newLocation The new location point of the user.
   * @return The new UserMetrics with updated values.
   */
  fun withUpdate(
      speed: Float,
      newAltitude: Double,
      bearing: Float,
      newLocation: LocationPoint,
  ): UserMetrics {
    var verticalSpeed = computeVerticalSpeed(newAltitude, newLocation)
    if (!verticalSpeed.isFinite()) {
      verticalSpeed = 0.0
    }
    return UserMetrics(speed, newAltitude, bearing, verticalSpeed, newLocation)
  }

  /**
   * Computes the vertical speed based on the new altitude and time since last update.
   *
   * @param newAltitude The new altitude of the user.
   * @param newLocation The new location point of the user.
   * @return The computed vertical speed.
   */
  private fun computeVerticalSpeed(newAltitude: Double, newLocation: LocationPoint): Double {
    return (newAltitude - this.altitude) / (newLocation.time - this.location.time)
  }

  override fun toString(): String {
    return "Horizontal Speed: %.2f m/s\nVertical Speed: %.2f m/s\nAltitude: %.0f m\nBearing: %.2f °"
        .format(speed, verticalSpeed, altitude, bearing)
  }
}
