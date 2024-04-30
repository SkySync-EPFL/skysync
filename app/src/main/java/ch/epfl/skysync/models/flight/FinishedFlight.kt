package ch.epfl.skysync.models.flight

import android.location.Location
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.user.Crew
import java.time.LocalDate
import java.time.LocalTime

data class FinishedFlight(
    override val id: String,
    override val nPassengers: Int,
    override val team: Team,
    override val flightType: FlightType,
    override val balloon: Balloon,
    override val basket: Basket,
    override val date: LocalDate,
    override val timeSlot: TimeSlot,
    override val vehicles: List<Vehicle>,
    val color: FlightColor = FlightColor.NO_COLOR,
    val takeOffTime: LocalTime,
    val takeOffLocation: Location,
    val landingTime: LocalTime,
    val landingLocation: Location,
    val meetupTimePassenger: LocalTime,
    val breakTime: Long, // time in milliseconds
    val vehicleProblems: List<String>,
    val littleBottle: UInt,
    val bigBottle: UInt,
    val prestigeBottle: UInt,
    val bottleToFarmer: Boolean,
    val filledByCrew: Crew
) : Flight {
  override fun getFlightStatus(): FlightStatus {
    return FlightStatus.COMPLETED
  }
}
