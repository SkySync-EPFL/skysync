package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.BasketSchema
import ch.epfl.skysync.models.flight.Basket

class BasketTable(db: FirestoreDatabase) :
    Table<Basket, BasketSchema>(db, BasketSchema::class, PATH) {

  /**
   * Add a new basket to the database
   *
   * @param item The basket to add to the database
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  fun add(item: Basket, onCompletion: (id: String) -> Unit, onError: (Exception) -> Unit) {
    db.addItem(path, BasketSchema.fromModel(item), onCompletion, onError)
  }

  companion object {
    const val PATH = "basket"
  }
}
