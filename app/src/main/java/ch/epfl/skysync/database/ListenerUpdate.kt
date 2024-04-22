package ch.epfl.skysync.database

/**
 * Represents the changes on a query listener
 *
 * @param isFirstUpdate If the listener is triggered for the first time, which correspond to simply
 *   fetching the query
 * @param isLocalUpdate If the listener is triggered by a local update, which happens on write
 *   operations
 *   ([doc](https://firebase.google.com/docs/firestore/query-data/listen#events-local-changes))
 * @param adds The items that have been added since the last update
 * @param updates The items that have been updated since the last update
 * @param deletes The items that have been deleted since the last update
 */
data class ListenerUpdate<T : Any>(
    val isFirstUpdate: Boolean,
    val isLocalUpdate: Boolean,
    val adds: Set<T>,
    val updates: Set<T>,
    val deletes: Set<T>
)
