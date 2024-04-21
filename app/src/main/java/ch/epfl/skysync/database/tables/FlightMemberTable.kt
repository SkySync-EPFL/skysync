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
   * @param onError Callback called when an error occurs
   */
  suspend fun add(item: FlightMemberSchema, onError: ((Exception) -> Unit)? = null): String {
    return withErrorCallback(onError) { db.addItem(path, item) }
  }
  /**
   * Update a flight member item
   *
   * This will override the existing item and replace it with the new one
   *
   * @param item The item to update
   * @param id the id of the item
   * @param onError Callback called when an error occurs
   */
  suspend fun update(id: String, item: FlightMemberSchema, onError: ((Exception) -> Unit)? = null) {
    return withErrorCallback(onError) { db.setItem(path, id, item) }
  }

  companion object {
    const val PATH = "flight-member"
  }
}
