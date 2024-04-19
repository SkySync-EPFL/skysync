package ch.epfl.skysync.database.tables

import android.os.SystemClock
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.schemas.FlightMemberSchema
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
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
    databaseSetup.clearDatabase(db)
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
  fun updateTest() {
    var flight: Flight? = null
    var isComplete = false
    var isError = false

    val flight1 = databaseSetup.flight1

    assertNotNull(flight1.id)

    val newTeam = Team(roles = listOf(Role(RoleType.PILOT, databaseSetup.pilot2)))
    val updateFlight1 = flight1.copy(nPassengers = flight1.nPassengers + 1, team = newTeam)

    flightTable.update(flight1.id, updateFlight1, { isComplete = true }, { isError = true })

    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, isComplete)
    assertEquals(false, isError)

    isComplete = false
    isError = false

    flightTable.get(
        flight1.id,
        {
          flight = it
          isComplete = true
        },
        { isError = true })

    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, isComplete)
    assertEquals(false, isError)
    assertEquals(updateFlight1, flight)

    isComplete = false
    isError = false
    var flightMembers: List<FlightMemberSchema>? = null
    flightMemberTable.getAll(
        {
          flightMembers = it
          isComplete = true
        },
        { isError = true })

    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, isComplete)
    assertEquals(false, isError)

    assertEquals(1, flightMembers?.size ?: 0)
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
