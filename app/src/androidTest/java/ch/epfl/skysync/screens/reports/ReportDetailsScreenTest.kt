package ch.epfl.skysync.screens.reports

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavHostController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.screens.admin.ReportDetailsScreen
import ch.epfl.skysync.viewmodel.FinishedFlightsViewModel
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ReportDetailsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private val navController: NavHostController = mockk(relaxed = true)
    private lateinit var finishedFlightsViewModel: FinishedFlightsViewModel
    private val db = FirestoreDatabase(useEmulator = true)
    private val dbs = DatabaseSetup()
    private val repository = Repository(db)

    @Before
    fun setUp() = runTest {
        dbs.clearDatabase(db)
        dbs.fillDatabase(db)
    }
    @Test
    fun showTitleAndMessageWhenEmpty(){

        runTest{

            composeTestRule.setContent {
                finishedFlightsViewModel =
                    FinishedFlightsViewModel.createViewModel(repository = repository, userId = dbs.crew2.id)
                finishedFlightsViewModel.refresh()
                finishedFlightsViewModel.getAllReports(dbs.finishedFlight1.id)
                ReportDetailsScreen(dbs.finishedFlight1.id,finishedFlightsViewModel,false,dbs.crew2.id,navController)
            }
        finishedFlightsViewModel.refreshUserAndFlights().join()

            }
        composeTestRule.onNodeWithText("No reports done right now").assertExists()
        composeTestRule.onNodeWithText("Report").assertExists()
    }
     @Test
    fun showReportsWhenNonEmpty(){

        runTest{

            composeTestRule.setContent {
                finishedFlightsViewModel =
                    FinishedFlightsViewModel.createViewModel(repository = repository, userId = dbs.pilot1.id)
                finishedFlightsViewModel.refresh()
                finishedFlightsViewModel.getAllReports(dbs.finishedFlight1.id)
                ReportDetailsScreen(dbs.finishedFlight1.id,finishedFlightsViewModel,true,dbs.pilot1.id,navController)
            }
        finishedFlightsViewModel.refreshUserAndFlights().join()

            }
        composeTestRule.onNodeWithText("No reports done right now").assertDoesNotExist()
        composeTestRule.onNodeWithText("Report").assertExists()
    }
}