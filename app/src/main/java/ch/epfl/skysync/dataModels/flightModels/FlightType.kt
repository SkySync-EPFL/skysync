package ch.epfl.skysync.dataModels.flightModels

data class FlightType(
    val name: String,
    val specialRoles: List<Role> = emptyList()
) {
    companion object{
        val DISCOVERY = FlightType("Discovery")
        val PREMIUM = FlightType("Premium")
        val FONDUE = FlightType("Fondue")
        val HIGH_ALTITUDE = FlightType("High Altitude")

    }
}






/**
 * Minimal set of roles present in any FlightType => PILOT and CREW
 */
val BASE_ROLES = listOf(
    Role(RoleType.PILOT),
    Role(RoleType.CREW),
)