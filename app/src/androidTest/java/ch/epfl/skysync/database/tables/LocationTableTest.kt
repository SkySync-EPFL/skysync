package ch.epfl.skysync.database.tables

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.MainCoroutineRule
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
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
    val originalLocation = Location(id = "loc1", value = LatLng(40.7128, -74.0060))
    val newLocation = Location(id = "loc1", value = LatLng(37.7749, -122.4194))
    locationTable.updateLocation(newLocation, onError = { assertNull(it) })

    val updatedLocation = locationTable.get("loc1", onError = { assertNull(it) })
    assertNotNull(updatedLocation)
    assertEquals(37.7749, updatedLocation!!.value.latitude, 0.001)
    assertEquals(-122.4194, updatedLocation.value.longitude, 0.001)
  }

  @Test
  fun listenForUpdatesTest() = runTest {
    val updates: MutableList<List<Location>> = mutableListOf()
    val userIds = listOf("user1", "user2")

    // Mocking real-time updates
    val listenerRegistrations =
        locationTable.listenForLocationUpdates(
            userIds, onChange = { locations -> updates.add(locations) }, coroutineScope = this)

    // Simulate location update
    val newLocation = Location(id = "user1", value = LatLng(34.0522, -118.2437))
    locationTable.updateLocation(newLocation)
    locationTable.listenForLocationUpdates(
        userIds, onChange = { locations -> updates.add(locations) }, coroutineScope = this)
    this.coroutineContext.job.children.forEach { it.join() } // Wait for all coroutines to finish

    assertTrue(updates.isNotEmpty())
    assertEquals(
        newLocation.value.latitude, updates.last().find { it.id == "user1" }?.value?.latitude)

    // Clean up listeners
    listenerRegistrations.forEach { it.remove() }
  }

  @Test
  fun deleteLocationTest() = runTest {
    val locationId = "loc1"
    locationTable.delete(locationId, onError = { assertNull(it) })
    val retrievedLocation = locationTable.get(locationId, onError = { assertNull(it) })
    assertNull(retrievedLocation)
  }
}
