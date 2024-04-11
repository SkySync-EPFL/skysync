package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.UNSET_ID

/**
 * represents a particular flight type
 *
 * @property name name of the type
 * @property specialRoles roles in addition to the BASE_ROLES(crew, pilot)
 */
data class FlightType(
    val name: String,
    val specialRoles: List<RoleType> = emptyList(),
    val id: String = UNSET_ID
) {
  companion object {
    val DISCOVERY = FlightType("Discovery")
    val PREMIUM = FlightType("Premium")
    val FONDUE = FlightType("Fondue", listOf(RoleType.MAITRE_FONDUE))
    val HIGH_ALTITUDE = FlightType("High Altitude", listOf(RoleType.OXYGEN_MASTER))
  }
}

/** Minimal set of roles present in each FlightType => PILOT and CREW */
val BASE_ROLES =
    listOf(
        RoleType.PILOT,
        RoleType.CREW,
    )
