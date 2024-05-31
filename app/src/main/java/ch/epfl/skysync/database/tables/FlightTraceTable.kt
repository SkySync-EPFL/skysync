package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.FlightTraceSchema
import ch.epfl.skysync.models.location.FlightTrace

/**
 * Represents the "flight trace" table in the database.
 *
 * @property db The FirestoreDatabase instance for interacting with the Firestore database.
 */
class FlightTraceTable(db: FirestoreDatabase) :
    Table<FlightTrace, FlightTraceSchema>(db, FlightTraceSchema::class, PATH) {

  /**
   * Set a new flight trace to the database
   *
   * @param item The flight trace to add to the database
   * @param id The id of the flight
   * @param onError Callback called when an error occurs
   */
  suspend fun set(id: String, item: FlightTrace, onError: ((Exception) -> Unit)? = null) {
    return withErrorCallback(onError) { db.setItem(path, id, FlightTraceSchema.fromModel(item)) }
  }

  companion object {
    const val PATH = "flight-trace"
  }
}
