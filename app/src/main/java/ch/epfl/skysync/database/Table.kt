package ch.epfl.skysync.database

import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Query.Direction
import kotlin.reflect.KClass
import kotlinx.coroutines.CoroutineScope

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
   * Wrap the [run] code in a try/catch block and, if given, call the [onError] callback when an
   * exception is thrown in [run], in any case throw the exception
   *
   * @param onError The callback called when an exception is thrown
   * @param run The code to run in the try/catch
   * @throws Exception Any exception thrown in [run]
   */
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
   * @param onError Callback called when an error occurs
   */
  open suspend fun get(id: String, onError: ((Exception) -> Unit)? = null): M? {
    return withErrorCallback(onError) { db.getItem(path, id, clazz)?.toModel() }
  }

  /**
   * Get all the items
   *
   * @param onError Callback called when an error occurs
   */
  open suspend fun getAll(onError: ((Exception) -> Unit)? = null): List<M> {
    return withErrorCallback(onError) { db.getAll(path, clazz).map { it.toModel() } }
  }

  /**
   * Query items based on a filter
   *
   * @param filter The filter to apply to the query
   * @param onError Callback called when an error occurs
   */
  open suspend fun query(
      filter: Filter,
      limit: Long? = null,
      orderBy: String? = null,
      orderByDirection: Direction = Query.Direction.ASCENDING,
      onError: ((Exception) -> Unit)? = null
  ): List<M> {
    return withErrorCallback(onError) {
      db.query(path, filter, clazz, limit, orderBy, orderByDirection).map { it.toModel() }
    }
  }

  /**
   * Delete an item
   *
   * @param id The id of the item
   * @param onError Callback called when an error occurs
   */
  open suspend fun delete(id: String, onError: ((Exception) -> Unit)? = null) {
    withErrorCallback(onError) { db.deleteItem(path, id) }
  }

  /**
   * Execute a query and delete the resulting items
   *
   * Note: this only delete the items, not their potential dependencies. To delete items with
   * dependencies, call [query], then [delete] for each item.
   *
   * @param filter The filter to apply to the query
   * @param onError Callback called when an error occurs
   */
  open suspend fun queryDelete(filter: Filter, onError: ((Exception) -> Unit)? = null) {
    withErrorCallback(onError) { db.queryDelete(path, filter) }
  }

  /**
   * Add a listener on a query
   *
   * The listener will be triggered each time the result of the query changes on the database.
   *
   * @param filter The filter to apply to the query
   * @param onChange Callback called each time the listener is triggered, passed the adds, updates,
   *   deletes that happened since the last listener trigger.
   * @param limit If specified, the maximum number of items to includes in the query
   * @param orderBy If specified, sort the items of the query by the given field
   * @param orderByDirection The direction to sort
   */
  open fun queryListener(
      filter: Filter,
      onChange: suspend (ListenerUpdate<S>) -> Unit,
      coroutineScope: CoroutineScope,
      limit: Long? = null,
      orderBy: String? = null,
      orderByDirection: Query.Direction = Query.Direction.ASCENDING,
  ): ListenerRegistration {
    return db.queryListener(
        path, filter, clazz, onChange, coroutineScope, limit, orderBy, orderByDirection)
  }

  /**
   * Delete the table
   *
   * This is only used for testing, as such it is only supported if using the emulator.
   *
   * @param onError Callback called when an error occurs
   */
  open suspend fun deleteTable(onError: ((Exception) -> Unit)? = null) {
    withErrorCallback(onError) { db.deleteTable(path) }
  }
}
