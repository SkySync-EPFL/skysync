package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.calendar.TimeSlot
import java.time.LocalDate

class PlannedFlight(
    override val id: String?,
    override val nPassengers: Int,
    override val team: Team = Team(Role.initRoles(BASE_ROLES)),
    override val flightType: FlightType,
    override val balloon: Balloon?,
    override val basket: Basket?,
    override val date: LocalDate,
    override val timeSlot: TimeSlot,
    override val vehicles: List<Vehicle>
) : Flight {
  fun readyToBeConfirmed(): Boolean {
    return team.isComplete() && nPassengers > 0 && balloon != null && basket != null
  }
}
