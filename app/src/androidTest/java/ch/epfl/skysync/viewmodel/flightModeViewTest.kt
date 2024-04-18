package ch.epfl.skysync.viewmodel

import ch.epfl.skysync.database.tables.DB_SLEEP_TIME
import ch.epfl.skysync.database.tables.FlightMemberTable
import ch.epfl.skysync.database.tables.FlightTable

import android.os.SystemClock
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.DatabaseSetup2
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.schemas.FlightMemberSchema
import ch.epfl.skysync.models.flight.Flight
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class flightModeViewTest {
    private val db = FirestoreDatabase(useEmulator = true)
    private val databaseSetup = DatabaseSetup()
    private val databaseSetupRemote = DatabaseSetup2()
    private val flightTable = FlightTable(db)
    private val flightMemberTable = FlightMemberTable(db)

    @get:Rule
    val composeTestRule = createComposeRule()

    lateinit var viewModel: FlightsViewModel
    @Before
    fun testSetup() {
        databaseSetup.clearDatabase(db)
        databaseSetup.fillDatabase(db)
        composeTestRule.setContent {
            viewModel = FlightsViewModel.createViewModel(flightTable)
        }
    }

    @Test
    fun initRemoteDB() {
        databaseSetupRemote.clearDatabase(db)
        //databaseSetupRemote.fillDatabase(db)
    }

    @Test
    fun getTest() {
        val flights = viewModel.currentFlights.value
        SystemClock.sleep(DB_SLEEP_TIME)
        assertEquals(flights.size, 1)
        assertEquals(flights[0], databaseSetup.flight1)
    }

    @Test
    fun deleteTest() {
        viewModel.deleteFlight(databaseSetup.flight1)
        SystemClock.sleep(DB_SLEEP_TIME)
        val flights = viewModel.currentFlights.value
        assertEquals(flights.size, 1)
    }
}
