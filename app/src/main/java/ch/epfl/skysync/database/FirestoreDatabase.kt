package ch.epfl.skysync.database

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.MemoryCacheSettings
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import java.lang.UnsupportedOperationException
import kotlin.reflect.KClass
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

/**
 * Represent a connection to a Firestore database
 *
 * @param useEmulator If true, it will try to connect to Firestore database of a local emulator
 */
class FirestoreDatabase(private val useEmulator: Boolean = false) {
  private val db = Firebase.firestore
  private val TAG = "Firebase"

  init {
    if (useEmulator) {
      // can only be called once but there is no method to check if already called
      try {
        db.useEmulator("10.0.2.2", 5002)
      } catch (_: IllegalStateException) {
        // this occurs when the FirebaseDatabase is instanced twice
      }
      db.firestoreSettings = firestoreSettings {
        setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
      }
    }
  }

  /**
   * Add a new item to the database
   *
   * This will generate a new id for this item and override any previously set id.
   *
   * @param path A filesystem-like path that specify the location of the table
   * @param item The item to add to the database, the types of the attributes have to be Firestore
   *   types
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  suspend fun <T : Any> addItem(
      path: String,
      item: T,
  ): String {
    val res = db.collection(path).add(item).await()
    Log.d(TAG, "Added $path/${res.id}")
    return res.id
  }

  /**
   * Set a new item to the database
   *
   * Set item at id and override any previously set id.
   *
   * @param path A filesystem-like path that specify the location of the table
   * @param item The item to set to the database, the types of the attributes have to be Firestore
   * @param id the id of the item
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  suspend fun <T : Any> setItem(
      path: String,
      id: String,
      item: T,
  ) {
    db.collection(path).document(id).set(item).await()
    Log.d(TAG, "Created $path/${id}")
  }

  /**
   * Get an item from the database
   *
   * @param path A filesystem-like path that specify the location of the table
   * @param id The id of the item
   * @param clazz The class of the item, this is used to reconstruct an instance of the item class
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  suspend fun <T : Any> getItem(
      path: String,
      id: String,
      clazz: KClass<T>,
  ): T? {
    val documentSnapshot = db.collection(path).document(id).get().await()

    Log.d(TAG, "Got $path/${documentSnapshot.id}")
    return documentSnapshot.toObject(clazz.java)
  }

  /**
   * Get all items from a table
   *
   * @param path A filesystem-like path that specify the location of the table
   * @param clazz The class of the item, this is used to reconstruct an instance of the item class
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  suspend fun <T : Any> getAll(
      path: String,
      clazz: KClass<T>,
  ): List<T> {
    val querySnapshot = db.collection(path).get().await()

    Log.d(TAG, "Got $path (x${querySnapshot.size()})")
    return querySnapshot.documents.mapNotNull {
      val res = it.toObject(clazz.java)
      if (res == null) {
        Log.w(TAG, "Casting failed for $path/${it.id}")
      }
      res
    }
  }

  /**
   * Query items from a table based on a filter
   *
   * @param path A filesystem-like path that specify the location of the table
   * @param filter The filter to apply to the query
   * @param clazz The class of the item, this is used to reconstruct an instance of the item class
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  suspend fun <T : Any> query(
      path: String,
      filter: Filter,
      clazz: KClass<T>,
  ): List<T> {
    val querySnapshot = db.collection(path).where(filter).get().await()

    Log.d(TAG, "Got $path (x${querySnapshot.size()})")
    return querySnapshot.documents.mapNotNull {
      val res = it.toObject(clazz.java)
      if (res == null) {
        Log.w(TAG, "Casting failed for $path/${it.id}")
      }
      res
    }
  }

  /**
   * Delete an item from the database
   *
   * @param path A filesystem-like path that specify the location of the table
   * @param id The id of the item
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  suspend fun deleteItem(
      path: String,
      id: String,
  ) {
    db.collection(path).document(id).delete().await()
    Log.d(TAG, "Deleted $path/$id")
  }

  /**
   * Execute a query on a table and delete the resulting items
   *
   * @param path A filesystem-like path that specify the location of the table
   * @param filter The filter to apply to the query
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  suspend fun queryDelete(
      path: String,
      filter: Filter,
  ) {
    coroutineScope {
      val querySnapshot = db.collection(path).where(filter).get().await()

      val deferreds =
          querySnapshot.documents.map { document ->
            async {
              document.reference.delete().await()
              Log.d(TAG, "Deleted $path/${document.id}")
            }
          }
      deferreds.awaitAll()
    }
  }

  /**
   * Delete a table (collection of items)
   *
   * This is only used for testing, as such it is only supported if using the emulator.
   *
   * @param path A filesystem-like path that specify the location of the table
   * @param onError Callback called when an error occurs
   */
  suspend fun deleteTable(path: String) {
    if (!useEmulator) {
      throw UnsupportedOperationException("Can only delete collection on the emulator.")
    }
    coroutineScope {
      val querySnapshot = db.collection(path).get().await()

      Log.d(TAG, "Delete table $path")
      val deferreds =
          querySnapshot.documents.map { document -> async { document.reference.delete().await() } }
      deferreds.awaitAll()
    }
  }
}
