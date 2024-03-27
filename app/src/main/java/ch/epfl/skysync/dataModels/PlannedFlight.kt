package ch.epfl.skysync.dataModels

import java.time.LocalDate
import java.util.Date

class PlannedFlight(
    override val id: Int,
    override val n_passengers: Int,
    override val team: Team,
    override val flightType: FlightType,
    override val balloon: Balloon?,
    override val basket: Basket?,
    override val date: LocalDate,
    override val isMorningFlight: Boolean,
    override val vehicles: List<Vehicle>
): Flight {
    init {
        if (team.hasNoRoles()) {
            team.roles.addAll(BASE_ROLES)
        }
    }

    fun readyToBeConfirmed(): Boolean {
        return team.isComplete()  &&
                n_passengers > 0 &&
                balloon != null &&
                basket != null
    }

}