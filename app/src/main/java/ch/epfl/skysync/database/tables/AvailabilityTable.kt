package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.AvailabilitySchema
import ch.epfl.skysync.models.calendar.Availability

/** Represent the "availability" table */
class AvailabilityTable(db: FirestoreDatabase) :
    Table<Availability, AvailabilitySchema>(db, AvailabilitySchema::class, PATH) {

  /**
   * Add a new availability to the database
   *
   * This will generate a new id for this availability and disregard any previously set id.
   *
   * @param userId The ID of the user whose availability it is
   * @param item The availability to add to the database
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  fun add(
      userId: String,
      item: Availability,
      onCompletion: (id: String) -> Unit,
      onError: (Exception) -> Unit
  ) {
    db.addItem(path, AvailabilitySchema.fromModel(userId, item), onCompletion, onError)
  }

  companion object {
    const val PATH = "availability"
  }
}
