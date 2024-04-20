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

  protected suspend fun <T> withErrorCallback(
      onError: ((Exception) -> Unit)?,
      run: suspend () -> T
  ): T {
    try {
      return run()
    } catch (e: Exception) {
      if (onError != null) {
        onError(e)
      }
      throw e
    }
  }

  /**
   * Get an item by ID
   *
   * @param id The id of the item
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  open suspend fun get(id: String, onError: ((Exception) -> Unit)? = null): M? {
    return db.getItem(path, id, clazz)?.toModel()
  }

  /**
   * Get all the items
   *
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  open suspend fun getAll(onError: ((Exception) -> Unit)? = null): List<M> {
    return db.getAll(path, clazz).map { it.toModel() }
  }

  /**
   * Query items based on a filter
   *
   * @param filter The filter to apply to the query
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  open suspend fun query(filter: Filter, onError: ((Exception) -> Unit)? = null): List<M> {
    return db.query(path, filter, clazz).map { it.toModel() }
  }

  /**
   * Delete an item
   *
   * @param id The id of the item
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  open suspend fun delete(id: String, onError: ((Exception) -> Unit)? = null) {
    db.deleteItem(path, id)
  }

  /**
   * Execute a query and delete the resulting items
   *
   * Note: this only delete the items, not their potential dependencies. To delete items with
   * dependencies, call [query], then [delete] for each item.
   *
   * @param filter The filter to apply to the query
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  open suspend fun queryDelete(filter: Filter, onError: ((Exception) -> Unit)? = null) {
    db.queryDelete(path, filter)
  }
  /**
   * Delete the table
   *
   * This is only used for testing, as such it is only supported if using the emulator.
   *
   * @param onError Callback called when an error occurs
   */
  open suspend fun deleteTable(onError: ((Exception) -> Unit)? = null) {
    db.deleteTable(path)
  }
}
