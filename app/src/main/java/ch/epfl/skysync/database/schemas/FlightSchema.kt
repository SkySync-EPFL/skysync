package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.DateUtility
import ch.epfl.skysync.database.FlightStatus
import ch.epfl.skysync.database.Schema
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.FlightColor
import ch.epfl.skysync.models.flight.PlannedFlight
import com.google.firebase.firestore.DocumentId
import java.lang.UnsupportedOperationException
import java.util.Date

data class FlightSchema(
    @DocumentId val id: String? = null,
    val flightTypeId: String? = null,
    /** Nullable */
    val balloonId: String? = null,
    /** Nullable */
    val basketId: String? = null,
    val vehicleIds: List<String>? = null,
    val status: FlightStatus? = null,
    // Note: this is called numPassengers and not nPassengers because
    // nPassengers is wrongly mapped to "npassengers" by the firestore class mapper
    // whereas numPassenger is correctly mapped as "numPassengers"
    // (which makes no sense but at least it works)
    val numPassengers: Int? = null,
    val timeSlot: TimeSlot? = null,
    /** We use the Date class instead of the LocalDate for Firestore see [DateUtility] */
    val date: Date? = null,
    /** In: Confirmed flight */
    val remarks: List<String>? = null,
    /** In: Confirmed flight */
    val color: FlightColor? = null,
    /** In: Confirmed flight */
    val meetupTimeTeam: String? = null,
    /** In: Confirmed flight */
    val departureTimeTeam: String? = null,
    /** In: Confirmed flight */
    val meetupTimePassenger: String? = null,
    /** In: Confirmed flight */
    val meetupLocationPassenger: String? = null,
    /** In: Confirmed flight */
    val isOngoing: Boolean? = null,
    /** In: Confirmed flight */
    val startTime: String? = null,
) : Schema<Flight> {
  override fun toModel(): Flight {
    throw NotImplementedError()
  }

  companion object {
    fun fromModel(model: Flight): FlightSchema {
      return when (model) {
        is PlannedFlight -> fromPlannedFlight(model)
        is ConfirmedFlight -> fromConfirmedFlight(model)
        else ->
            throw UnsupportedOperationException("Unexpected class ${model.javaClass.simpleName}")
      }
    }

    private fun fromPlannedFlight(flight: PlannedFlight): FlightSchema {
      return FlightSchema(
          id = flight.id,
          flightTypeId = flight.flightType.id,
          balloonId = flight.balloon?.id,
          basketId = flight.basket?.id,
          vehicleIds = flight.vehicles.map { it.id },
          status = FlightStatus.PLANNED,
          numPassengers = flight.nPassengers,
          timeSlot = flight.timeSlot,
          date = DateUtility.localDateToDate(flight.date),
      )
    }

    private fun fromConfirmedFlight(flight: ConfirmedFlight): FlightSchema {
      return FlightSchema(
          id = flight.id,
          flightTypeId = flight.flightType.id,
          balloonId = flight.balloon.id,
          basketId = flight.basket.id,
          vehicleIds = flight.vehicles.map { it.id },
          status = FlightStatus.CONFIRMED,
          numPassengers = flight.nPassengers,
          timeSlot = flight.timeSlot,
          date = DateUtility.localDateToDate(flight.date),
          remarks = flight.remarks,
          color = flight.color,
          meetupTimeTeam = DateUtility.localTimeToString(flight.meetupTimeTeam),
          departureTimeTeam = DateUtility.localTimeToString(flight.departureTimeTeam),
          meetupTimePassenger = DateUtility.localTimeToString(flight.meetupTimePassenger),
          meetupLocationPassenger = flight.meetupLocationPassenger,
          isOngoing = flight.isOngoing,
          startTime =
              if (flight.startTime != null) DateUtility.localTimeToString(flight.startTime)
              else null,
      )
    }
  }
}
