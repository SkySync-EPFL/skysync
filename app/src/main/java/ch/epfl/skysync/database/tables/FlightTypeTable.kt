package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.FlightTypeSchema
import ch.epfl.skysync.models.flight.FlightType

class FlightTypeTable(db: FirestoreDatabase) :
    Table<FlightType, FlightTypeSchema>(db, FlightTypeSchema::class, PATH) {

  /**
   * Add a new flight type to the database
   *
   * @param item The flight type to add to the database
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  fun add(item: FlightType, onCompletion: (id: String) -> Unit, onError: (Exception) -> Unit) {
    db.addItem(path, FlightTypeSchema.fromModel(item), onCompletion, onError)
  }

  companion object {
    const val PATH = "flight-type"
  }
}
