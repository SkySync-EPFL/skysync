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
    /**
     * Update a flight member item
     *
     * This will override the existing item and replace it with the new one
     *
     * @param item The item to update
     * @param id the id of the item
     * @param onCompletion Callback called on completion of the operation
     * @param onError Callback called when an error occurs
     */
    fun update(
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
