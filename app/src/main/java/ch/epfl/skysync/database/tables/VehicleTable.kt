package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.DateUtility
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.VehicleSchema
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.Vehicle
import com.google.firebase.firestore.Filter
import java.time.LocalDate
import kotlinx.coroutines.coroutineScope

/**
 * Represents the "vehicle" table in the database.
 *
 * @property db The FirestoreDatabase instance for interacting with the Firestore database.
 */
class VehicleTable(db: FirestoreDatabase) :
    Table<Vehicle, VehicleSchema>(db, VehicleSchema::class, PATH) {

  /**
   * Add a new vehicle to the database
   *
   * @param item The vehicle to add to the database
   * @param onError Callback called when an error occurs
   */
  suspend fun add(item: Vehicle, onError: ((Exception) -> Unit)? = null): String {
    return withErrorCallback(onError) { db.addItem(path, VehicleSchema.fromModel(item)) }
  }
  /**
   * Returns the available vehicles on a given date and timeslot
   *
   * @param localDate The requested day
   * @param timeslot The requested timeslot
   * @param onError Callback called when an error occurs
   */
  suspend fun getVehiclesAvailableOn(
      flightTable: FlightTable,
      localDate: LocalDate,
      timeslot: TimeSlot,
      onError: ((Exception) -> Unit)?
  ): List<Vehicle> = coroutineScope {
    withErrorCallback(onError) {
      val dateFilter = Filter.equalTo("date", DateUtility.localDateToDate(localDate))
      val timeslotFilter = Filter.equalTo("timeSlot", timeslot)
      val flightFilter = Filter.and(dateFilter, timeslotFilter)

      val unavailableVehicleIds: List<String> =
          flightTable
              .query(flightFilter)
              .flatMap { flight: Flight -> flight.vehicles }
              .map { vehicle: Vehicle -> vehicle.id }

      getAll().filterNot { vehicle: Vehicle -> vehicle.id in unavailableVehicleIds }
    }
  }

  companion object {
    const val PATH = "vehicle"
  }
}
