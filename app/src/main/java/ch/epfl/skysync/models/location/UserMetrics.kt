package ch.epfl.skysync.models.location

data class UserMetrics(
    val speed: Float,
    val altitude: Double,
    val bearing: Float,
    val verticalSpeed: Double,
    val location: LocationPoint,
) {
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

  /** computes the vertical speed based on the new altitude and time since last update */
  private fun computeVerticalSpeed(newAltitude: Double, newLocation: LocationPoint): Double {
    return (newAltitude - this.altitude) / (newLocation.time - this.location.time)
  }

  override fun toString(): String {
    return "Horizontal Speed: %.2f m/s\nVertical Speed: %.2f m/s\nAltitude: %.0f m\nBearing: %.2f °"
        .format(speed, verticalSpeed, altitude, bearing)
  }
}
