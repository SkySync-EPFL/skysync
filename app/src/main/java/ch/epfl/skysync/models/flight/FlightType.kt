package ch.epfl.skysync.models.flight

/**
 * represents a particular flight type
 *
 * @property name name of the type
 * @property specialRoles roles in addition to the BASE_ROLES(crew, pilot)
 */
data class FlightType(val name: String, val specialRoles: List<RoleType> = emptyList()) {
  companion object {
    val DISCOVERY = FlightType("Discovery")
    val PREMIUM = FlightType("Premium")
    val FONDUE = FlightType("Fondue", listOf(RoleType.MAITRE_FONDUE))
    val HIGH_ALTITUDE = FlightType("High Altitude", listOf(RoleType.OXYGEN_MASTER))
    val all_flights =
        listOf(
            DISCOVERY,
            PREMIUM,
            FONDUE,
            HIGH_ALTITUDE,
        )
  }
}

/** Minimal set of roles present in each FlightType => PILOT and CREW */
val BASE_ROLES =
    listOf(
        RoleType.PILOT,
        RoleType.CREW,
    )

enum class FlightTypeTest(val name_val: String, val specialRoles: List<RoleType> = emptyList()) {
  DISCOVERY("Discovery"),
  PREMIUM("Premium"),
  FONDUE("Fondue", listOf(RoleType.MAITRE_FONDUE)),
  HIGH_ALTITUDE("High Altitude", listOf(RoleType.OXYGEN_MASTER))
}
