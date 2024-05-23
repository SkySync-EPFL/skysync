package ch.epfl.skysync.viewmodel

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavHostController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.reports.PilotReport
import ch.epfl.skysync.models.user.Pilot
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
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
    finishedFlightsViewModel = FinishedFlightsViewModel(repository, dbSetup.pilot1.id)
    finishedFlightsViewModel.refresh()
    val user = finishedFlightsViewModel.currentUser
    composeTestRule.waitUntil { user.value != null }
    assert(user.value is Pilot)
    assert(user.value!!.id == dbSetup.pilot1.id)
  }

  @Test
  fun loadsRightFlights() = runTest {
    finishedFlightsViewModel = FinishedFlightsViewModel(repository, dbSetup.pilot1.id)
    finishedFlightsViewModel.refresh()
    composeTestRule.waitUntil { finishedFlightsViewModel.currentUser.value != null }
    composeTestRule.waitUntil { finishedFlightsViewModel.currentFlights.value != null }
    assert(finishedFlightsViewModel.currentFlights.value != null)
    assert(finishedFlightsViewModel.currentFlights.value!!.size == 2)
  }

  @Test
  fun loadsRightFlightsForAdmin() = runTest {
    finishedFlightsViewModel = FinishedFlightsViewModel(repository, dbSetup.admin1.id)
    finishedFlightsViewModel.refresh()
    composeTestRule.waitUntil { finishedFlightsViewModel.currentUser.value != null }
    composeTestRule.waitUntil { finishedFlightsViewModel.currentFlights.value != null }
    assert(finishedFlightsViewModel.currentFlights.value != null)
    assert(finishedFlightsViewModel.currentFlights.value!!.size == 2)
  }

  @Test
  fun addFlightWorksCorrectly() = runTest {
    finishedFlightsViewModel = FinishedFlightsViewModel(repository, dbSetup.pilot1.id)
    finishedFlightsViewModel.refresh()
    val flight = dbSetup.finishedFlight1
    finishedFlightsViewModel.addFlight(flight)
    composeTestRule.waitUntil { finishedFlightsViewModel.currentFlights.value != null }
    assert(finishedFlightsViewModel.currentFlights.value!!.size == 3)
  }

  @Test
  fun addReportWorksCorrectly() = runTest {
    finishedFlightsViewModel = FinishedFlightsViewModel(repository, dbSetup.pilot1.id)
    val report =
        PilotReport(
            id = UNSET_ID,
            author = dbSetup.pilot1.id,
            effectivePax = 2,
            takeOffTime = dbSetup.takeOffTime2,
            landingTime = dbSetup.landingTime2,
            takeOffLocation = dbSetup.takeOffLocation1,
            landingLocation = dbSetup.landingLocation1,
            begin = dbSetup.takeOffTime2,
            end = dbSetup.landingTime2,
            pauseDuration = 0,
            comments = "Some comments",
        )
    finishedFlightsViewModel.refresh()
    val flight = dbSetup.finishedFlight1
    finishedFlightsViewModel.getAllReports(flight.id).join()
    val nbReportBefore = finishedFlightsViewModel.flightReports.value!!.size
    finishedFlightsViewModel.addReport(report, flight.id).join()
    finishedFlightsViewModel.getAllReports(flight.id).join()
    assertEquals(nbReportBefore + 1, finishedFlightsViewModel.flightReports.value!!.size)
  }

  @Test
  fun viewReportsWorksCorrectly() = runTest {
    finishedFlightsViewModel = FinishedFlightsViewModel(repository, dbSetup.pilot1.id)
    val flight = dbSetup.finishedFlight1
    finishedFlightsViewModel.refresh()
    finishedFlightsViewModel.getAllReports(flight.id).join()
    val reports = finishedFlightsViewModel.flightReports.value
    assertNotEquals(null, reports)
    assertEquals(2, reports!!.size)
  }
}
