package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.DateLocalDateConverter
import ch.epfl.skysync.database.FlightStatus
import ch.epfl.skysync.database.Schema
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.PlannedFlight
import com.google.firebase.firestore.DocumentId
import java.lang.UnsupportedOperationException
import java.util.Date

data class FlightSchema(
    @DocumentId val id: String? = null,
    val flightTypeId: String? = null,
    val balloonId: String? = null,
    val basketId: String? = null,
    val vehicleIds: List<String>? = null,
    val status: FlightStatus? = null,
    // Note: this is called numPassengers and not nPassengers because
    // nPassengers is wrongly mapped to "npassengers" by the firestore class mapper
    // whereas numPassenger is correctly mapped as "numPassengers"
    // (which makes no sense but at least it works)
    val numPassengers: Int? = null,
    val timeSlot: TimeSlot? = null,
    /** We use the Date class instead of the LocalDate for Firestore see [DateLocalDateConverter] */
    val date: Date? = null
) : Schema<Flight> {
  override fun toModel(): Flight {
    throw NotImplementedError()
  }

  companion object {
    fun fromModel(model: Flight): FlightSchema {
      val status =
          when (model) {
            is PlannedFlight -> FlightStatus.PLANNED
            else ->
                throw UnsupportedOperationException(
                    "Unexpected class ${model.javaClass.simpleName}")
          }
      return FlightSchema(
          id = model.id,
          flightTypeId = model.flightType.id,
          balloonId = model.balloon?.id,
          basketId = model.basket?.id,
          vehicleIds = model.vehicles.map { it.id },
          status = status,
          numPassengers = model.nPassengers,
          timeSlot = model.timeSlot,
          date = DateLocalDateConverter.localDateToDate(model.date),
      )
    }
  }
}
