package ch.epfl.skysync.viewmodel

import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.FinishedFlight
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
class InFlightViewModelTest {

  @get:Rule val composeTestRule = createComposeRule()
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  private val repository: Repository = Repository(db)
  private val flightTable = repository.flightTable
  private val flightTraceTable = repository.flightTraceTable
  private val locationTable = repository.locationTable
  private lateinit var inFlightViewModel: InFlightViewModel

  @Before
  fun testSetUp() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
    composeTestRule.setContent {
      inFlightViewModel = InFlightViewModel.createViewModel(repository)
      val countString by inFlightViewModel.counter.collectAsStateWithLifecycle()
      Text(countString)
    }
    inFlightViewModel.init(dbs.pilot1.id).join()
  }

  @Test
  fun timerIsZeroBeforeStart() {
    val countString = inFlightViewModel.counter.value
    assertTrue(countString == "00:00:00")
  }

  @Test
  fun finishedFlightIsSavedOnStop() = runTest {
    inFlightViewModel.setCurrentFlight(dbs.flight4.id)
    inFlightViewModel.startFlight().join()
    composeTestRule.waitUntil { inFlightViewModel.isOngoingFlight() }
    inFlightViewModel.stopFlight().join()
    val flight = flightTable.get(dbs.flight4.id, onError = { assertNull(it) })
    assertFalse(flight is ConfirmedFlight)
    assertTrue(flight is FinishedFlight)
  }

  @Test
  fun testStartFunction() = runTest {
    inFlightViewModel.setCurrentFlight(dbs.flight4.id)
    inFlightViewModel.startFlight().join()

    // we should not be able to start display flight trace
    inFlightViewModel.startDisplayFlightTrace(dbs.finishedFlight1)
    assert(!inFlightViewModel.isDisplayTrace())

    composeTestRule.waitUntil(timeoutMillis = 1500) {
      val countString = inFlightViewModel.counter.value
      countString == "00:00:01"
    }
    assertEquals(InFlightViewModel.FlightStage.ONGOING, inFlightViewModel.flightStage.value)
  }

  @Test
  fun testStopFunction() = runTest {
    inFlightViewModel.setCurrentFlight(dbs.flight4.id)
    inFlightViewModel.startFlight().join()
    composeTestRule.waitUntil(timeoutMillis = 2500) {
      val countString = inFlightViewModel.counter.value
      countString == "00:00:02"
    }
    inFlightViewModel.stopFlight().join()
    assertEquals(InFlightViewModel.FlightStage.POST, inFlightViewModel.flightStage.value)
  }

  @Test
  fun testInvalidStateOperations() = runTest {
    inFlightViewModel.startFlight().join()
    assertEquals(InFlightViewModel.FlightStage.IDLE, inFlightViewModel.flightStage.value)
    inFlightViewModel.stopFlight().join()
    assertEquals(InFlightViewModel.FlightStage.IDLE, inFlightViewModel.flightStage.value)
    inFlightViewModel.clearFlight().join()
    assertEquals(InFlightViewModel.FlightStage.IDLE, inFlightViewModel.flightStage.value)
    inFlightViewModel.quitDisplayFlightTrace()
    assertEquals(InFlightViewModel.FlightStage.IDLE, inFlightViewModel.flightStage.value)
  }

  @Test
  fun testInitialLocationSetup() {
    assertNotNull(inFlightViewModel.currentLocations)
  }

  @Test
  fun testLocationUpdate() = runTest {
    val flight = flightTable.get(dbs.flight4.id, onError = { assertNull(it) }) as ConfirmedFlight

    inFlightViewModel.setCurrentFlight(flight.id)

    inFlightViewModel.startFlight().join()

    // having the program working with out of order location updates is not a strict requirement
    // but it's still nice to have
    inFlightViewModel
        .addLocation(Location(userId = dbs.pilot1.id, point = LocationPoint(0, 0.0, 0.0)))
        .join()
    inFlightViewModel
        .addLocation(Location(userId = dbs.crew1.id, point = LocationPoint(2, 0.0, 0.0)))
        .join()
    inFlightViewModel
        .addLocation(Location(userId = dbs.crew2.id, point = LocationPoint(2, 0.0, 0.0)))
        .join()
    inFlightViewModel
        .addLocation(Location(userId = dbs.pilot1.id, point = LocationPoint(3, 0.0, 0.0)))
        .join()
    inFlightViewModel
        .addLocation(Location(userId = dbs.crew1.id, point = LocationPoint(1, 0.0, 0.0)))
        .join()
    inFlightViewModel
        .addLocation(Location(userId = dbs.pilot1.id, point = LocationPoint(0, 0.0, 0.0)))
        .join()

    val locations = inFlightViewModel.currentLocations.value

    // all the flight members should have an entry as they all have had new updates
    assertEquals(setOf(dbs.pilot1.id, dbs.crew1.id, dbs.crew2.id), locations.keys)

    // verify that the current location is the one with the highest time
    assertEquals(3, locations[dbs.pilot1.id]!!.second.point.time)
    assertEquals(2, locations[dbs.crew1.id]!!.second.point.time)
    assertEquals(2, locations[dbs.crew2.id]!!.second.point.time)

    inFlightViewModel.stopFlight().join()
    inFlightViewModel.clearFlight().join()

    val pilotLocations =
        locationTable.query(Filter.equalTo("userId", dbs.pilot1.id), onError = { assertNull(it) })

    // verify that the current user locations are deleted at the end of the flight
    assertEquals(listOf<Location>(), pilotLocations)
  }

  /**
   * Verify that pilot locations that have been added before the user startFlight on his screen are
   * correctly fetched
   */
  @Test
  fun testFetchPreviousFlightLocation() = runTest {
    val flight = flightTable.get(dbs.flight4.id, onError = { assertNull(it) }) as ConfirmedFlight

    // location with to high time (here 127) should not be taken into account as they are
    // leftover locations from a previous flight
    locationTable.addLocation(
        Location(userId = dbs.pilot1.id, point = LocationPoint(127, 12.0, -3.0)),
        onError = { assertNull(it) })
    locationTable.addLocation(
        Location(userId = dbs.pilot1.id, point = LocationPoint(0, 0.0, 0.0)),
        onError = { assertNull(it) })
    locationTable.addLocation(
        Location(userId = dbs.pilot1.id, point = LocationPoint(1, 1.0, 0.0)),
        onError = { assertNull(it) })
    locationTable.addLocation(
        Location(userId = dbs.pilot1.id, point = LocationPoint(2, 2.0, 0.0)),
        onError = { assertNull(it) })

    inFlightViewModel.setCurrentFlight(flight.id)

    inFlightViewModel.startFlight().join()

    locationTable.addLocation(
        Location(userId = dbs.pilot1.id, point = LocationPoint(3, 3.0, 0.0)),
        onError = { assertNull(it) })
    locationTable.addLocation(
        Location(userId = dbs.pilot1.id, point = LocationPoint(4, 4.0, 0.0)),
        onError = { assertNull(it) })

    inFlightViewModel.stopFlight().join()

    val locations = inFlightViewModel.flightLocations.value

    assertEquals(
        listOf(
            LocationPoint(0, 0.0, 0.0),
            LocationPoint(1, 1.0, 0.0),
            LocationPoint(2, 2.0, 0.0),
            LocationPoint(3, 3.0, 0.0),
            LocationPoint(4, 4.0, 0.0),
        ),
        locations.map { it.point })
  }

  @Test
  fun testSaveFlightTrace() = runTest {
    val flight = flightTable.get(dbs.flight4.id, onError = { assertNull(it) }) as ConfirmedFlight

    inFlightViewModel.setCurrentFlight(flight.id)
    inFlightViewModel.startFlight().join()

    // here we need to have the update in order to have all the locations in the flight trace
    // as the locations that are out of order are discarded (which is a feature not a bug...)
    inFlightViewModel
        .addLocation(Location(userId = dbs.pilot1.id, point = LocationPoint(0, 12.0, 0.0)))
        .join()
    inFlightViewModel
        .addLocation(Location(userId = dbs.crew1.id, point = LocationPoint(2, 13.0, 0.0)))
        .join()
    inFlightViewModel
        .addLocation(Location(userId = dbs.crew2.id, point = LocationPoint(2, -13.03, 0.0)))
        .join()
    inFlightViewModel
        .addLocation(Location(userId = dbs.pilot1.id, point = LocationPoint(2, 0.0, -12.0)))
        .join()
    inFlightViewModel
        .addLocation(Location(userId = dbs.pilot1.id, point = LocationPoint(3, 1.0, 0.0)))
        .join()

    inFlightViewModel.stopFlight().join()

    val flightTrace = flightTraceTable.get(dbs.flight4.id, onError = { assertNull(it) })

    assertEquals(
        FlightTrace(
            id = dbs.flight4.id,
            trace =
                listOf(
                    LocationPoint(0, 12.0, 0.0),
                    LocationPoint(2, 0.0, -12.0),
                    LocationPoint(3, 1.0, 0.0),
                ),
        ),
        flightTrace)
  }

  @Test
  fun testDisplayFlightTrace() = runTest {
    val flightTrace =
        FlightTrace(
            trace =
                listOf(
                    LocationPoint(0, 0.0, 0.0),
                    LocationPoint(1, 1.0, 0.0),
                    LocationPoint(2, 2.0, 0.0),
                    LocationPoint(3, 3.0, 0.0),
                ))
    flightTraceTable.set(dbs.finishedFlight1.id, flightTrace, onError = { assertNull(it) })

    assertEquals(0, inFlightViewModel.flightLocations.value.size)

    inFlightViewModel.startDisplayFlightTrace(dbs.finishedFlight1).join()

    assertEquals(InFlightViewModel.FlightStage.DISPLAY, inFlightViewModel.flightStage.value)

    val locations = inFlightViewModel.flightLocations.value

    assertEquals(flightTrace.trace, locations.map { it.point })

    inFlightViewModel.quitDisplayFlightTrace()

    assertEquals(InFlightViewModel.FlightStage.IDLE, inFlightViewModel.flightStage.value)
    assertEquals(0, inFlightViewModel.flightLocations.value.size)
  }
}
