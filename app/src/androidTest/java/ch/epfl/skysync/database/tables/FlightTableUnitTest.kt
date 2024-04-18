package ch.epfl.skysync.database.tables

import android.os.SystemClock
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.schemas.FlightMemberSchema
import ch.epfl.skysync.models.flight.Flight
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FlightTableUnitTest {
  private val db = FirestoreDatabase(useEmulator = true)
  private val databaseSetup = DatabaseSetup()
  private val flightTable = FlightTable(db)
  private val flightMemberTable = FlightMemberTable(db)

  @Before
  fun testSetup() {
    //databaseSetup.clearDatabase(db)
    databaseSetup.fillDatabase(db)
  }

  @Test
  fun getTest() {
    var flight: Flight? = null
    var isComplete = false
    var isError = false

    flightTable.get(
        databaseSetup.flight1.id,
        {
          flight = it
          isComplete = true
        },
        { isError = true })

    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, isComplete)
    assertEquals(false, isError)
    assertEquals(databaseSetup.flight1, flight)
  }


  @Test
  fun deleteTest() {
    var isComplete = false
    var isError = false

    flightTable.delete(databaseSetup.flight1.id, { isComplete = true }, { isError = true })

    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, isComplete)
    assertEquals(false, isError)

    var flightMembers: List<FlightMemberSchema>? = null
    isComplete = false
    isError = false

    flightMemberTable.getAll(
        {
          flightMembers = it
          isComplete = true
        },
        { isError = true })

    var flights: List<Flight>? = null
    var isCompleteFlight = false
    var isErrorFlight = false

    flightTable.getAll(
        {
          flights = it
          isCompleteFlight = true
        },
        { isErrorFlight = true })

    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, isComplete)
    assertEquals(false, isError)
    assertEquals(listOf<FlightMemberSchema>(), flightMembers)

    assertEquals(true, isCompleteFlight)
    assertEquals(false, isErrorFlight)
    assertEquals(listOf<Flight>(), flights)
  }
}
