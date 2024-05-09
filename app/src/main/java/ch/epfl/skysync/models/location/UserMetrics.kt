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
      altitude: Double,
      bearing: Float,
      location: LocationPoint,
  ): UserMetrics {
    var verticalSpeed = (altitude - this.altitude) / (location.time - this.location.time)
    if (!verticalSpeed.isFinite()) {
      verticalSpeed = 0.0
    }
    return UserMetrics(speed, altitude, bearing, verticalSpeed, location)
  }

  override fun toString(): String {
    return "Horizontal Speed: %.2f m/s\nVertical Speed: %.2f m/s\nAltitude: %.0f m\nBearing: %.2f °"
        .format(speed, verticalSpeed, altitude, bearing)
  }
}
