package ch.epfl.skysync.database

import com.google.firebase.firestore.Filter
import kotlin.reflect.KClass

/**
 * Represent a table in the database
 *
 * @param db The Firestore database connection
 * @param clazz The class of the schema of the table
 * @param path A filesystem-like path that specify the location of the table
 */
abstract class Table<M, S : Schema<M>>(
    protected val db: FirestoreDatabase,
    protected val clazz: KClass<S>,
    protected val path: String
) {

  /**
   * Get an item by ID
   *
   * @param id The id of the item
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  open fun get(id: String, onCompletion: (M?) -> Unit, onError: (Exception) -> Unit) {
    db.getItem(path, id, clazz, { onCompletion(it?.toModel()) }, onError)
  }

  /**
   * Get all the items
   *
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  open fun getAll(onCompletion: (List<M>) -> Unit, onError: (Exception) -> Unit) {
    db.getAll(path, clazz, { schemas -> onCompletion(schemas.map { it.toModel() }) }, onError)
  }

  /**
   * Query items based on a filter
   *
   * @param filter The filter to apply to the query
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  open fun query(filter: Filter, onCompletion: (List<M>) -> Unit, onError: (Exception) -> Unit) {
    db.query(
        path, filter, clazz, { schemas -> onCompletion(schemas.map { it.toModel() }) }, onError)
  }

  /**
   * Delete an item
   *
   * @param id The id of the item
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  open fun delete(id: String, onCompletion: () -> Unit, onError: (java.lang.Exception) -> Unit) {
    db.deleteItem(path, id, onCompletion, onError)
  }

  /**
   * Delete the table
   *
   * This is only used for testing, as such it is only supported if using the emulator.
   *
   * @param onError Callback called when an error occurs
   */
  open fun deleteTable(onError: (Exception) -> Unit) {
    db.deleteTable(path, onError)
  }
}
