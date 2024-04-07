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
   * @param personId The ID of the person whose availability it is
   * @param item The availability to add to the database
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  fun add(
      personId: String,
      item: Availability,
      onCompletion: (id: String) -> Unit,
      onError: (Exception) -> Unit
  ) {
    db.addItem(path, AvailabilitySchema.fromModel(personId, item), onCompletion, onError)
  }

  companion object {
    const val PATH = "availability"
  }
}
