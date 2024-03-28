package ch.epfl.skysync.database

import java.lang.Exception
import kotlin.reflect.KClass

/** Represent a database "connection" to an in-memory hashmap. */
class MockDatabase : Database {
  private var idCounter = 0
  private var state = hashMapOf<String, Any>()

  fun getState(): HashMap<String, Any> {
    return state
  }

  private fun getKey(path: String, id: String): String {
    return "$path/$id"
  }

  override fun <T : Any> add(
      path: String,
      item: T,
      onCompletion: () -> Unit,
      onError: (Exception) -> Unit
  ) {
    val key = getKey(path, idCounter.toString())
    state[key] = item
    idCounter += 1
    onCompletion()
  }

  override fun <T : Any> get(
      path: String,
      id: String,
      clazz: KClass<T>,
      onCompletion: (T?) -> Unit,
      onError: (Exception) -> Unit
  ) {
    val key = getKey(path, id)
    val item = state[key]
    if (item == null) {
      onCompletion(null)
    } else if (clazz.isInstance(item)) {
      onCompletion(item as T?)
    } else {
      onError(
          ClassCastException(
              "$key: Expected type ${clazz.simpleName} got ${item.javaClass.simpleName}"))
    }
  }

  override fun delete(
      path: String,
      id: String,
      onCompletion: () -> Unit,
      onError: (Exception) -> Unit
  ) {
    val key = getKey(path, id)
    state.remove(key)
    onCompletion()
  }
}
