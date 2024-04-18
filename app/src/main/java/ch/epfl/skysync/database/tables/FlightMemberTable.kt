package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.FlightMemberSchema

/** Represent the "flight-member" relation */
class FlightMemberTable(db: FirestoreDatabase) :
    Table<FlightMemberSchema, FlightMemberSchema>(db, FlightMemberSchema::class, PATH) {

  /**
   * Add a new flight member to the database
   *
   * @param item The flight member to add to the database
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  fun add(
      item: FlightMemberSchema,
      onCompletion: (id: String) -> Unit,
      onError: (Exception) -> Unit
  ) {
    db.addItem(path, item, onCompletion, onError)
  }

  fun set(
      id: String,
      item: FlightMemberSchema,
      onCompletion: () -> Unit,
      onError: (Exception) -> Unit
  ) {
    db.setItem(path, id, item, onCompletion, onError)
  }

  companion object {
    const val PATH = "flight-member"
  }
}
