package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.BalloonSchema
import ch.epfl.skysync.models.flight.Balloon

/** Represent the "balloon" table */
class BalloonTable(db: FirestoreDatabase) :
    Table<Balloon, BalloonSchema>(db, BalloonSchema::class, PATH) {

  /**
   * Add a new balloon to the database
   *
   * @param item The balloon to add to the database
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  suspend fun add(item: Balloon, onError: ((Exception) -> Unit)? = null): String {
    return withErrorCallback(onError) { db.addItem(path, BalloonSchema.fromModel(item)) }
  }

  companion object {
    const val PATH = "balloon"
  }
}
