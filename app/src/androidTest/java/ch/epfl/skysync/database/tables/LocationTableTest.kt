package ch.epfl.skysync.database.tables

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.MainCoroutineRule
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.ListenerUpdate
import ch.epfl.skysync.models.location.Location
import ch.epfl.skysync.models.location.LocationPoint
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
  fun addLocationTest() = runTest {
    val location = Location(userId = dbs.pilot1.id, point = LocationPoint(0, 40.7128, -74.0060))
    val id = locationTable.addLocation(location, onError = { assertNull(it) })
    val getLocation = locationTable.get(id, onError = { assertNull(it) })

    assertNotNull(getLocation)
    assertEquals(location.copy(id = id), getLocation)
  }

  @Test
  fun listenForUpdatesTest() = runTest {
    val updates: MutableList<ListenerUpdate<Location>> = mutableListOf()
    val userIds = listOf(dbs.pilot2.id, dbs.admin2.id)

    val listenerRegistrations =
        userIds.map { userId ->
          locationTable.listenForLocationUpdates(
              userId, onChange = { update -> updates.add(update) }, coroutineScope = this)
        }

    // query the database to let the time for the listener to get their first update
    locationTable.getAll()
    locationTable.getAll()

    val newLocation1 =
        Location(userId = dbs.pilot2.id, point = LocationPoint(0, 34.0522, -118.2437))
    locationTable.addLocation(newLocation1)

    val newLocation2 = Location(userId = dbs.admin2.id, point = LocationPoint(3, 0.0, 0.0))
    locationTable.addLocation(newLocation2)

    this.coroutineContext.job.children.forEach { it.join() } // Wait for all coroutines to finish
    updates.forEach { println(it) }

    assertEquals(4, updates.size)
    assertEquals(newLocation1.point, updates[2].adds[0].point)
    assertEquals(newLocation2.point, updates[3].adds[0].point)

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
