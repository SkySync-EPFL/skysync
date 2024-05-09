package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.ListenerUpdate
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.LocationSchema
import ch.epfl.skysync.models.location.Location
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope

class LocationTable(db: FirestoreDatabase) :
    Table<Location, LocationSchema>(db, LocationSchema::class, PATH) {

  /**
   * Add a new location of a user to the database.
   *
   * @param location The location to add
   * @param onError Callback called when an error occurs
   */
  suspend fun addLocation(location: Location, onError: ((Exception) -> Unit)? = null): String {
    return withErrorCallback(onError) { db.addItem(path, LocationSchema.fromModel(location)) }
  }

  /**
   * Listen for real-time updates to locations in the database.
   *
   * @param onChange Callback called each time there is an update.
   * @param coroutineScope CoroutineScope for launching asynchronous tasks.
   */
  fun listenForLocationUpdates(
      userId: String,
      onChange: suspend (ListenerUpdate<Location>) -> Unit,
      coroutineScope: CoroutineScope,
  ): ListenerRegistration {
    return queryListener(
        Filter.equalTo("userId", userId),
        limit = 1,
        orderBy = "time",
        orderByDirection = Query.Direction.DESCENDING,
        onChange = { update ->
          onChange(
              ListenerUpdate(
                  isFirstUpdate = update.isFirstUpdate,
                  isLocalUpdate = update.isLocalUpdate,
                  adds = update.adds.map { it.toModel() },
                  updates = update.updates.map { it.toModel() },
                  deletes = update.deletes.map { it.toModel() },
              ))
        },
        coroutineScope = coroutineScope)
  }

  companion object {
    const val PATH = "location"
  }
}
