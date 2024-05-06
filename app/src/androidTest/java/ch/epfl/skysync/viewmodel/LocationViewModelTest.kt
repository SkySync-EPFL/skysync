package ch.epfl.skysync.viewmodel

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.location.FlightTrace
import ch.epfl.skysync.models.location.Location
import ch.epfl.skysync.models.location.LocationPoint
import com.google.firebase.firestore.Filter
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocationViewModelTest {

  @get:Rule val composeTestRule = createComposeRule()
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  private val repository: Repository = Repository(db)
  private val flightTable = repository.flightTable
  private val flightTraceTable = repository.flightTraceTable
  private val locationTable = repository.locationTable
  private lateinit var locationViewModel: LocationViewModel

  @Before
  fun testSetUp() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
    composeTestRule.setContent {
      locationViewModel = LocationViewModel.createViewModel(dbs.pilot1.id, repository = repository)
    }
  }

  @Test
  fun testInitialLocationSetup() = runTest { assertNotNull(locationViewModel.currentLocations) }

  @Test
  fun testLocationUpdate() = runTest {
    val flight = flightTable.get(dbs.flight4.id, onError = { assertNull(it) }) as ConfirmedFlight

    locationViewModel.startFlight(flight)

    // having the program working with out of order location updates is not a strict requirement
    // but it's still nice to have
    locationViewModel
        .addLocation(Location(userId = dbs.pilot1.id, data = LocationPoint(0, 0.0, 0.0)))
        .join()
    locationViewModel
        .addLocation(Location(userId = dbs.crew1.id, data = LocationPoint(2, 0.0, 0.0)))
        .join()
    locationViewModel
        .addLocation(Location(userId = dbs.crew2.id, data = LocationPoint(2, 0.0, 0.0)))
        .join()
    locationViewModel
        .addLocation(Location(userId = dbs.pilot1.id, data = LocationPoint(3, 0.0, 0.0)))
        .join()
    locationViewModel
        .addLocation(Location(userId = dbs.crew1.id, data = LocationPoint(1, 0.0, 0.0)))
        .join()
    locationViewModel
        .addLocation(Location(userId = dbs.pilot1.id, data = LocationPoint(0, 0.0, 0.0)))
        .join()

    val locations = locationViewModel.currentLocations.value

    // all the flight members should have an entry as they all have had new updates
    assertEquals(setOf(dbs.pilot1.id, dbs.crew1.id, dbs.crew2.id), locations.keys)

    // verify that the current location is the one with the highest time
    assertEquals(3, locations[dbs.pilot1.id]!!.second.data.time)
    assertEquals(2, locations[dbs.crew1.id]!!.second.data.time)
    assertEquals(2, locations[dbs.crew2.id]!!.second.data.time)

    locationViewModel.endFlight().join()

    val pilotLocations =
        locationTable.query(Filter.equalTo("userId", dbs.pilot1.id), onError = { assertNull(it) })

    // verify that the current user locations are deleted at the end of the flight
    assertEquals(listOf<Location>(), pilotLocations)
  }

  @Test
  fun testSaveFlightTrace() = runTest {
    val flight = flightTable.get(dbs.flight4.id, onError = { assertNull(it) }) as ConfirmedFlight

    locationViewModel.startFlight(flight)

    // here we need to have the update in order to have all the locations in the flight trace
    // as the locations that are out of order are discarded (which is a feature not a bug...)
    locationViewModel
        .addLocation(Location(userId = dbs.pilot1.id, data = LocationPoint(0, 12.0, 0.0)))
        .join()
    locationViewModel
        .addLocation(Location(userId = dbs.crew1.id, data = LocationPoint(2, 13.0, 0.0)))
        .join()
    locationViewModel
        .addLocation(Location(userId = dbs.crew2.id, data = LocationPoint(2, -13.03, 0.0)))
        .join()
    locationViewModel
        .addLocation(Location(userId = dbs.pilot1.id, data = LocationPoint(2, 0.0, -12.0)))
        .join()
    locationViewModel
        .addLocation(Location(userId = dbs.pilot1.id, data = LocationPoint(3, 1.0, 0.0)))
        .join()

    locationViewModel.saveFlightTrace().join()

    val flightTrace = flightTraceTable.get(dbs.flight4.id, onError = { assertNull(it) })

    assertEquals(
        FlightTrace(
            id = dbs.flight4.id,
            data =
                listOf(
                    LocationPoint(0, 12.0, 0.0),
                    LocationPoint(2, 0.0, -12.0),
                    LocationPoint(3, 1.0, 0.0),
                ),
        ),
        flightTrace)
  }
}
