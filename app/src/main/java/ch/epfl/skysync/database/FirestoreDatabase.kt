package ch.epfl.skysync.database

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MemoryCacheSettings
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.toObject
import java.lang.UnsupportedOperationException
import kotlin.reflect.KClass
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
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
   * @param limit If specified, the maximum number of items to includes in the query
   * @param orderBy If specified, sort the items of the query by the given field
   * @param orderByDirection The direction to sort
   */
  suspend fun <T : Any> query(
      path: String,
      filter: Filter,
      clazz: KClass<T>,
      limit: Long? = null,
      orderBy: String? = null,
      orderByDirection: Query.Direction = Query.Direction.ASCENDING,
  ): List<T> {
    var query = db.collection(path).where(filter)
    if (orderBy != null) {
      query = query.orderBy(orderBy, orderByDirection)
    }
    if (limit != null) {
      query = query.limit(limit)
    }

    val querySnapshot = query.get().await()

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
   */
  suspend fun queryDelete(
      path: String,
      filter: Filter,
  ) {
    coroutineScope {
      val querySnapshot = db.collection(path).where(filter).get().await()

      querySnapshot.documents
          .map { document ->
            launch {
              document.reference.delete().await()
              Log.d(TAG, "Deleted $path/${document.id}")
            }
          }
          .forEach { it.join() }
    }
  }

  /**
   * Add a listener on a query
   *
   * The listener will be triggered each time the result of the query changes on the database.
   *
   * @param path A filesystem-like path that specify the location of the table
   * @param filter The filter to apply to the query
   * @param clazz The class of the item, this is used to reconstruct an instance of the item class
   * @param onChange Callback called each time the listener is triggered, passed the adds, updates,
   *   deletes that happened since the last listener trigger.
   * @param limit If specified, the maximum number of items to includes in the query
   * @param orderBy If specified, sort the items of the query by the given field
   * @param orderByDirection The direction to sort
   */
  fun <T : Any> queryListener(
      path: String,
      filter: Filter,
      clazz: KClass<T>,
      onChange: (ListenerUpdate<T>) -> Unit,
      limit: Long? = null,
      orderBy: String? = null,
      orderByDirection: Query.Direction = Query.Direction.ASCENDING,
  ): ListenerRegistration {
    var query = db.collection(path).where(filter)
    if (orderBy != null) {
      query = query.orderBy(orderBy, orderByDirection)
    }
    if (limit != null) {
      query = query.limit(limit)
    }

    var isFirstUpdate = true
    val listener =
        query.addSnapshotListener { snapshot, e ->
          if (e != null) {
            Log.e(TAG, "Listen failed ($path).", e)
            return@addSnapshotListener
          }

          if (snapshot == null) {
            Log.d(TAG, "Listen null ($path)")
            return@addSnapshotListener
          }
          val adds = mutableListOf<T>()
          val updates = mutableListOf<T>()
          val deletes = mutableListOf<T>()
          for (dc in snapshot.documentChanges) {
            when (dc.type) {
              DocumentChange.Type.ADDED -> adds.add(dc.document.toObject(clazz.java))
              DocumentChange.Type.MODIFIED -> updates.add(dc.document.toObject(clazz.java))
              DocumentChange.Type.REMOVED -> deletes.add(dc.document.toObject(clazz.java))
            }
          }
          val listenerUpdate =
              ListenerUpdate(
                  isFirstUpdate = isFirstUpdate,
                  isLocalUpdate = snapshot.metadata.hasPendingWrites(),
                  adds = adds,
                  updates = updates,
                  deletes = deletes)

          Log.d(TAG, "Listen update: x${adds.size} x${updates.size} x${deletes.size} ($path)")

          isFirstUpdate = false
          onChange(listenerUpdate)
        }
    Log.d(TAG, "Added listener ($path)")
    return listener
  }

  /**
   * Delete a table (collection of items)
   *
   * This is only used for testing, as such it is only supported if using the emulator.
   *
   * @param path A filesystem-like path that specify the location of the table
   */
  suspend fun deleteTable(path: String) {
    if (!useEmulator) {
      throw UnsupportedOperationException("Can only delete collection on the emulator.")
    }
    coroutineScope {
      val querySnapshot = db.collection(path).get().await()

      Log.d(TAG, "Delete table $path")
      querySnapshot.documents
          .map { document -> launch { document.reference.delete().await() } }
          .forEach { it.join() }
    }
  }
}
