package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.DateUtility
import ch.epfl.skysync.database.FlightStatus
import ch.epfl.skysync.database.Schema
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.FinishedFlight
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
    val reportId: List<String>? = null,
) : Schema<Flight> {
  override fun toModel(): Flight {
    throw NotImplementedError()
  }

  companion object {
    fun fromModel(model: Flight): FlightSchema {
      return when (model) {
        is PlannedFlight ->
            FlightSchema(
                id = model.id,
                flightTypeId = model.flightType.id,
                balloonId = model.balloon?.id,
                basketId = model.basket?.id,
                vehicleIds = model.vehicles.map { it.id },
                status = FlightStatus.PLANNED,
                numPassengers = model.nPassengers,
                timeSlot = model.timeSlot,
                date = DateUtility.localDateToDate(model.date),
            )
        is ConfirmedFlight ->
            FlightSchema(
                id = model.id,
                flightTypeId = model.flightType.id,
                balloonId = model.balloon?.id,
                basketId = model.basket?.id,
                vehicleIds = model.vehicles.map { it.id },
                status = FlightStatus.CONFIRMED,
                numPassengers = model.nPassengers,
                timeSlot = model.timeSlot,
                date = DateUtility.localDateToDate(model.date),
                remarks = model.remarks,
                color = model.color,
                meetupTimeTeam = DateUtility.localTimeToString(model.meetupTimeTeam),
                departureTimeTeam = DateUtility.localTimeToString(model.departureTimeTeam),
                meetupTimePassenger = DateUtility.localTimeToString(model.meetupTimePassenger),
                meetupLocationPassenger = model.meetupLocationPassenger,
            )
          is FinishedFlight ->
              FlightSchema(
                  id = model.id,
                  flightTypeId = model.flightType.id,
                  balloonId = model.balloon.id,
                  basketId = model.basket.id,
                  vehicleIds = model.vehicles.map { it.id },
                  status = FlightStatus.FINISHED,
                  numPassengers = model.nPassengers,
                  timeSlot = model.timeSlot,
                  date = DateUtility.localDateToDate(model.date),
                  color = FlightColor.NO_COLOR,
                    takeOffTime = DateUtility.localTimeToString(model.takeOffTime) ,
                    takeOffLocationLat = model.takeOffLocation.latitude,
                    takeOffLocationLong = model.takeOffLocation.latitude,
                    landingTime = DateUtility.localTimeToString(model.landingTime),
                    landingLocationLat = model.landingLocation.latitude,
                    landingLocationLong = model.landingLocation.longitude,
                    flightTime = model.flightTime,
                    reportId = model.reportId.map { it.id },
              )
        else ->
            throw UnsupportedOperationException("Unexpected class ${model.javaClass.simpleName}")
      }
    }
  }
}
