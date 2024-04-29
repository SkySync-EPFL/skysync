package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.LocationSchema
import ch.epfl.skysync.models.location.Location
import kotlinx.coroutines.CoroutineScope
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch


class LocationTable(
    db: FirestoreDatabase
) : Table<Location, LocationSchema>(db, LocationSchema::class, "location") {

    suspend fun updateLocation(location: Location) {
        val schema = LocationSchema.fromModel(location)
        db.setItem("location", location.id, schema)
    }

    fun listenForLocationUpdates(
        userIds: List<String>,
        onChange: suspend (List<Location>) -> Unit,
        coroutineScope: CoroutineScope
    ): List<ListenerRegistration> {
        return userIds.map { userId ->
            queryListener(
                Filter.equalTo("id", userId),
                onChange = { update -> coroutineScope.launch {
                    val locations = update.updates.map { it.toModel() }
                    onChange(locations)
                }},
                coroutineScope = coroutineScope
            )
        }
    }
}