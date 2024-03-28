package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.Database
import ch.epfl.skysync.database.schemas.AvailabilitySchema
import ch.epfl.skysync.models.calendar.Availability

/** Represent the "availability" table */
class AvailabilityTable(private val db: Database) {

  /**
   * Get an availability by ID
   *
   * @param id The id of the availability
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  fun get(id: String, onCompletion: (Availability?) -> Unit, onError: (Exception) -> Unit) {
    db.get(PATH, id, AvailabilitySchema::class, { onCompletion(it?.toModel()) }, onError)
  }

  /**
   * Add a new availability to the database
   *
   * This will generate a new id for this availability and override any previously set id.
   *
   * @param personId The ID of the person whose availability it is
   * @param item The availability to add to the database
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  fun add(
      personId: String,
      item: Availability,
      onCompletion: () -> Unit,
      onError: (Exception) -> Unit
  ) {
    db.add(PATH, AvailabilitySchema.fromModel(personId, item), { onCompletion() }, onError)
  }

  companion object {
    const val PATH = "availability"
  }
}
