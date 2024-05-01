package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.LocationSchema
import ch.epfl.skysync.models.location.Location
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class LocationTable(db: FirestoreDatabase) :
    Table<Location, LocationSchema>(db, LocationSchema::class, PATH) {

  /**
   * Update the location of a user in the database.
   *
   * @param location The location to update.
   * @param onError Optional error handler for catching exceptions.
   */
  suspend fun updateLocation(location: Location, onError: ((Exception) -> Unit)? = null) {
    val schema = LocationSchema.fromModel(location)
    return withErrorCallback(onError) { db.setItem(path, location.id, schema) }
  }

  /**
   * Listen for real-time updates to locations in the database.
   *
   * @param onChange Callback called each time there is an update.
   * @param coroutineScope CoroutineScope for launching asynchronous tasks.
   */
  fun listenForLocationUpdates(
      userIds: List<String>,
      onChange: suspend (List<Location>) -> Unit,
      coroutineScope: CoroutineScope,
  ): List<ListenerRegistration> {
    return userIds.map { userId ->
      queryListener(
          Filter.equalTo("uid", userId),
          onChange = { update ->
            coroutineScope.launch {
              val locations = update.updates.map { it.toModel() }
              onChange(locations)
            }
          },
          coroutineScope = coroutineScope)
    }
  }

  companion object {
    const val PATH = "location"
  }
}
