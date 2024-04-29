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
   * @param onError Callback called when an error occurs
   */
  suspend fun add(
      userId: String,
      item: Availability,
      onError: ((Exception) -> Unit)? = null
  ): String {
    return withErrorCallback(onError) {
      db.addItem(path, AvailabilitySchema.fromModel(userId, item))
    }
  }

  /**
   * Update a availability
   *
   * This will overwrite the availability at the given id.
   *
   * @param userId The ID of the user whose availability it is
   * @param availabilityId The id of the availability to update
   * @param item The new availability item
   * @param onError Callback called when an error occurs
   */
  suspend fun update(
      userId: String,
      availabilityId: String,
      item: Availability,
      onError: ((Exception) -> Unit)? = null
  ) {
    return withErrorCallback(onError) {
      db.setItem(path, availabilityId, AvailabilitySchema.fromModel(userId, item))
    }
  }

  companion object {
    const val PATH = "availability"
  }
}
