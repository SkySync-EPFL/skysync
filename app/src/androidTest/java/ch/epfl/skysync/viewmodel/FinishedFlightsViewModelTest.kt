package ch.epfl.skysync.viewmodel

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavHostController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.models.user.Pilot
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FinishedFlightsViewModelTest {
  @get:Rule val composeTestRule = createComposeRule()
  val navController: NavHostController = mockk("NavController", relaxed = true)
  private lateinit var finishedFlightsViewModel: FinishedFlightsViewModel
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbSetup = DatabaseSetup()
  private val repository: Repository = Repository(db)

  @Before
  fun setupHistory() = runTest {
    dbSetup.clearDatabase(db)
    dbSetup.fillDatabase(db)
  }

  @Test
  fun loadsRightUser() = runTest {
    finishedFlightsViewModel = FinishedFlightsViewModel(repository, "id-pilot-1")
    finishedFlightsViewModel.refresh()
    val user = finishedFlightsViewModel.currentUser
    composeTestRule.waitUntil { user.value != null }
    assert(user.value is Pilot)
    assert(user.value!!.id == "id-pilot-1")
  }

  @Test
  fun loadsRightFlights() = runTest {
    finishedFlightsViewModel = FinishedFlightsViewModel(repository, dbSetup.pilot1.id)
    finishedFlightsViewModel.refresh()
    composeTestRule.waitUntil { finishedFlightsViewModel.currentUser.value != null }
    val flights = finishedFlightsViewModel.getFlights()
    assert(flights != null)
    assert(flights!!.size == 2)
  }

  @Test
  fun loadsRightFlightsForAdmin() = runTest {
    finishedFlightsViewModel = FinishedFlightsViewModel(repository, dbSetup.admin1.id)
    finishedFlightsViewModel.refresh()
    composeTestRule.waitUntil { finishedFlightsViewModel.currentUser.value != null }
    val flights = finishedFlightsViewModel.getFlights()
    assert(flights != null)
    assert(flights!!.size == 2)
  }

  /*
  @Test
  fun getFlightByLocationWorksCorrectly() = runTest {
    finishedFlightsViewModel = FinishedFlightsViewModel(repository, "id-pilot-1")
    finishedFlightsViewModel.refresh()
    val flight = finishedFlightsViewModel.getFlightByLocation("0.0")
    composeTestRule.waitUntil { flight != null }
    assert(flight!!.size == 2)
  }*/

  /*@Test
  fun selectedFlightIsCorrect() = runTest {
      finishedFlightsViewModel = FinishedFlightsViewModel(repository, "id-pilot-1")
      finishedFlightsViewModel.refresh()
      finishedFlightsViewModel.selectFlight(dbSetup.flight5.id)
      val selectedFlight = finishedFlightsViewModel.selectedFlight
      composeTestRule.waitUntil { selectedFlight.value != null }
      assert(selectedFlight.value == dbSetup.flight5)
  }*/
}
