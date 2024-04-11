package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.VehicleSchema
import ch.epfl.skysync.models.flight.Vehicle

/** Represent the "vehicle" table */
class VehicleTable(db: FirestoreDatabase) :
    Table<Vehicle, VehicleSchema>(db, VehicleSchema::class, PATH) {

  /**
   * Add a new vehicle to the database
   *
   * @param item The vehicle to add to the database
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  fun add(item: Vehicle, onCompletion: (id: String) -> Unit, onError: (Exception) -> Unit) {
    db.addItem(path, VehicleSchema.fromModel(item), onCompletion, onError)
  }

  companion object {
    const val PATH = "vehicle"
  }
}
