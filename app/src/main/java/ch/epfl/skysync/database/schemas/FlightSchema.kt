package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.DateUtility
import ch.epfl.skysync.database.DateUtility.dateToHourMinuteString
import ch.epfl.skysync.database.DbFlightStatus
import ch.epfl.skysync.database.Schema
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.FlightColor
import ch.epfl.skysync.models.flight.PlannedFlight
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import java.lang.UnsupportedOperationException
import java.util.Date

// @get:PropertyName("...") is a fix for when a field name gets changed during serialization
// see: https://stackoverflow.com/questions/38681260/firebase-propertyname-doesnt-work

data class FlightSchema(
    @DocumentId val id: String? = null,
    val flightTypeId: String? = null,
    /** Nullable */
    val balloonId: String? = null,
    /** Nullable */
    val basketId: String? = null,
    val vehicleIds: List<String>? = null,
    val status: DbFlightStatus? = null,
    @get:PropertyName("nPassengers") val nPassengers: Int? = null,
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
    @get:PropertyName("isOngoing") val isOngoing: Boolean? = null,
    /** In: Confirmed flight Nullable */
    val startTimestamp: Long? = null,
    /** In: Finished flight */
    val takeOffTime: String? = null,
    /** In: Finished flight */
    val takeOffLocationLat: Double? = null,
    /** In: Finished flight */
    val takeOffLocationLong: Double? = null,
    /** In: Finished flight */
    val landingTime: String? = null,
    /** In: Finished flight */
    val landingLocationLat: Double? = null,
    /** In: Finished flight */
    val landingLocationLong: Double? = null,
    /** In: Finished flight */
    val flightTime: Long? = null,
    /** In: Finished flight */
    val reportIds: List<String>? = null,
) : Schema<Flight> {
  override fun toModel(): Flight {
    throw NotImplementedError()
  }

  companion object {
    fun fromModel(model: Flight): FlightSchema {
      return when (model) {
        is PlannedFlight -> fromPlannedFlight(model)
        is ConfirmedFlight -> fromConfirmedFlight(model)
        is FinishedFlight -> fromFinishedFlight(model)
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
          status = DbFlightStatus.PLANNED,
          nPassengers = flight.nPassengers,
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
          status = DbFlightStatus.CONFIRMED,
          nPassengers = flight.nPassengers,
          timeSlot = flight.timeSlot,
          date = DateUtility.localDateToDate(flight.date),
          remarks = flight.remarks,
          color = flight.color,
          meetupTimeTeam = DateUtility.localTimeToString(flight.meetupTimeTeam),
          departureTimeTeam = DateUtility.localTimeToString(flight.departureTimeTeam),
          meetupTimePassenger = DateUtility.localTimeToString(flight.meetupTimePassenger),
          meetupLocationPassenger = flight.meetupLocationPassenger,
          isOngoing = flight.isOngoing,
          startTimestamp = flight.startTimestamp,
      )
    }

    /** fills the data from a [FinishedFlight] into a [FlightSchema] */
    private fun fromFinishedFlight(flight: FinishedFlight): FlightSchema =
        FlightSchema(
            id = flight.id,
            flightTypeId = flight.flightType.id,
            balloonId = flight.balloon.id,
            basketId = flight.basket.id,
            vehicleIds = flight.vehicles.map { it.id },
            status = DbFlightStatus.FINISHED,
            nPassengers = flight.nPassengers,
            timeSlot = flight.timeSlot,
            date = DateUtility.localDateToDate(flight.date),
            color = flight.color,
            takeOffTime = dateToHourMinuteString(flight.takeOffTime),
            takeOffLocationLat = flight.takeOffLocation.latitude,
            takeOffLocationLong = flight.takeOffLocation.longitude,
            landingTime = dateToHourMinuteString(flight.landingTime),
            landingLocationLat = flight.landingLocation.latitude,
            landingLocationLong = flight.landingLocation.longitude,
            flightTime = flight.flightTime,
            reportIds = flight.reportId.map { it.id })
  }
}
