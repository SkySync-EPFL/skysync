package ch.epfl.skysync.viewmodel

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.models.location.Location
import com.google.android.gms.maps.model.LatLng
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
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
  private lateinit var locationViewModel: LocationViewModel

  @Before
  fun restSetUp() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
    composeTestRule.setContent {
      locationViewModel = LocationViewModel.createViewModel(repository = repository)
    }
  }

  @Test fun testInitialLocationSetup() = runTest { assertNotNull(locationViewModel.locations) }

  @Test
  fun testLocationUpdate() = runTest {
    val testLocation = Location(dbs.pilot1.id, LatLng(10.0, 10.0))

    // First, set up the listener
    locationViewModel.fetchAndListenToLocations(listOf(dbs.pilot1.id))

    // Then, update the location
    locationViewModel.updateMyLocation(testLocation)

    // Wait for all launched coroutines in the ViewModel to complete
    advanceUntilIdle()

    // Check if the updated location is now part of the observed locations
    val locations = locationViewModel.locations.value
    assertTrue("The expected location was not found in the ViewModel.", locations.contains(testLocation))
  }
}
