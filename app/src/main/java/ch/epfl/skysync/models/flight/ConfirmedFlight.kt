package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.calendar.TimeSlot
import java.time.LocalDate
import java.time.LocalTime

data class ConfirmedFlight(
    override val id: String,
    override val nPassengers: Int,
    override val team: Team,
    override val flightType: FlightType,
    override val balloon: Balloon,
    override val basket: Basket,
    override val date: LocalDate,
    override val timeSlot: TimeSlot,
    override val vehicles: List<Vehicle>,
    val remarks: List<String>,
    val color: FlightColor = FlightColor.NO_COLOR,
    val meetupTimeTeam: LocalTime,
    val departureTimeTeam: LocalTime,
    val meetupTimePassenger: LocalTime,
    val meetupLocationPassenger: String,
) : Flight {
    override fun getFlightStatus(): FlightStatus {
        return FlightStatus.CONFIRMED
    }
}
