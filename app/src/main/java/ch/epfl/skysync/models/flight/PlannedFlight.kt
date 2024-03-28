package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.calendar.TimeSlot
import java.time.LocalDate

class PlannedFlight(
    override val id: Int,
    override val nPassengers: Int,
    override val team: Team,
    override val flightType: FlightType,
    override val balloon: Balloon?,
    override val basket: Basket?,
    override val date: LocalDate,
    override val timeSlot: TimeSlot,
    override val vehicles: List<Vehicle>
): Flight {
    init {
        if (team.hasNoRoles()) {
            team.roles.addAll(BASE_ROLES)
        }
    }

    fun readyToBeConfirmed(): Boolean {
        return team.isComplete()  &&
                nPassengers > 0 &&
                balloon != null &&
                basket != null
    }

}