package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.BasketSchema
import ch.epfl.skysync.models.flight.Basket

/** Represent the "basket" table */
class BasketTable(db: FirestoreDatabase) :
    Table<Basket, BasketSchema>(db, BasketSchema::class, PATH) {

  /**
   * Add a new basket to the database
   *
   * @param item The basket to add to the database
   * @param onError Callback called when an error occurs
   */
  suspend fun add(item: Basket, onError: ((Exception) -> Unit)? = null): String {
    return withErrorCallback(onError) { db.addItem(path, BasketSchema.fromModel(item)) }
  }

  companion object {
    const val PATH = "basket"
  }
}
