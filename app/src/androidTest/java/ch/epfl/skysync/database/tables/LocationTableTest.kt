package ch.epfl.skysync.database.tables

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.MainCoroutineRule
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.ListenerUpdate
import ch.epfl.skysync.models.location.Location
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.job
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocationTableTest {
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  private val locationTable = LocationTable(db)

  @ExperimentalCoroutinesApi @get:Rule var mainCoroutineRule = MainCoroutineRule()

  @Before
  fun testSetup() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
  }

  @Test
  fun updateLocationTest() = runTest {
    val originalLocation = Location(id = dbs.pilot1.id, value = LatLng(40.7128, -74.0060))
    locationTable.updateLocation(originalLocation)
    val newLocation = Location(id = dbs.crew1.id, value = LatLng(37.7749, -122.4194))
    locationTable.updateLocation(newLocation, onError = { assertNull(it) })

    val updatedLocation = locationTable.get(dbs.crew1.id, onError = { assertNull(it) })
    assertNotNull(updatedLocation)
    assertEquals(37.7749, updatedLocation!!.value.latitude, 0.001)
    assertEquals(-122.4194, updatedLocation.value.longitude, 0.001)
  }

  @Test
  fun listenForUpdatesTest() = runTest {
    val updates: MutableList<ListenerUpdate<Location>> = mutableListOf()
    val userIds = listOf(dbs.pilot2.id, dbs.admin2.id)

    // Mocking real-time updates
    val listenerRegistrations =
        userIds.map { userId ->
          locationTable.listenForLocationUpdates(
              userId, onChange = { update -> updates.add(update) }, coroutineScope = this)
        }

    val newLocation = Location(id = dbs.pilot2.id, value = LatLng(34.0522, -118.2437))
    locationTable.updateLocation(newLocation)

    val updatedLocation = Location(id = dbs.pilot2.id, value = LatLng(0.0, 0.0))
    locationTable.updateLocation(updatedLocation)

    this.coroutineContext.job.children.forEach { it.join() } // Wait for all coroutines to finish

    // The order of the listener triggers is:
    // - Add new location
    // - Initial listener query result
    // - update location
    // as the first update as it needs to wait for a requests and is not
    // a coroutine (it is not blocking as it is executed by the listener)
    // while the listener is triggered before the add operation is performed
    // see doc (https://firebase.google.com/docs/firestore/query-data/listen#events-local-changes)
    assertEquals(3, updates.size)
    assertEquals(newLocation.value.latitude, updates[0].adds[0].value.latitude, 0.001)
    assertEquals(updatedLocation.value.latitude, updates[2].updates[0].value.latitude, 0.001)

    // Clean up listeners
    listenerRegistrations.forEach { it.remove() }
  }

  @Test
  fun deleteLocationTest() = runTest {
    val locationId = dbs.pilot1.id
    locationTable.delete(locationId, onError = { assertNull(it) })
    val retrievedLocation = locationTable.get(locationId, onError = { assertNull(it) })
    assertNull(retrievedLocation)
  }
}
