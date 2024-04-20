package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.FlightTypeSchema
import ch.epfl.skysync.models.flight.FlightType

/** Represent the "flight-type" table */
class FlightTypeTable(db: FirestoreDatabase) :
    Table<FlightType, FlightTypeSchema>(db, FlightTypeSchema::class, PATH) {

  /**
   * Add a new flight type to the database
   *
   * @param item The flight type to add to the database
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  suspend fun add(item: FlightType, onError: ((Exception) -> Unit)? = null): String {
    return withErrorCallback(onError) { db.addItem(path, FlightTypeSchema.fromModel(item)) }
  }

  companion object {
    const val PATH = "flight-type"
  }
}
