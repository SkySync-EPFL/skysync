package ch.epfl.skysync.database.tables

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.schemas.FlightMemberSchema
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FlightTableUnitTest {
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  private val flightTable = FlightTable(db)
  private val flightMemberTable = FlightMemberTable(db)

  @Before
  fun testSetup() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
  }

  @Test
  fun getTest() = runTest {
    val flight = flightTable.get(dbs.flight1.id, onError = { assertNull(it) })
    assertEquals(dbs.flight1, flight)
  }

  @Test
  fun updateTest() = runTest {
    val newTeam = Team(roles = listOf(Role(RoleType.PILOT, dbs.pilot2)))
    val updateFlight1 = dbs.flight1.copy(nPassengers = dbs.flight1.nPassengers + 1, team = newTeam)

    flightTable.update(dbs.flight1.id, updateFlight1, onError = { assertNull(it) })

    val flight = flightTable.get(dbs.flight1.id, onError = { assertNull(it) })

    assertEquals(updateFlight1, flight)

    val flightMembers =
        flightMemberTable.getAll(onError = { assertNull(it) }).filter { flightMemberSchema ->
          flightMemberSchema.flightId == dbs.flight1.id
        }

    assertEquals(1, flightMembers.size)
    assertEquals(dbs.flight1.id, flightMembers[0].flightId)
    assertEquals(dbs.pilot2.id, flightMembers[0].userId)
  }

  @Test
  fun deleteTest() = runTest {
    flightTable.delete(dbs.flight1.id, onError = { assertNull(it) })

    val flightMembers =
        flightMemberTable.getAll(onError = { assertNull(it) }).filter { flightMemberSchema ->
          flightMemberSchema.flightId == dbs.flight1.id
        }

    val flights = flightTable.getAll(onError = { assertNull(it) })

    assertEquals(listOf<FlightMemberSchema>(), flightMembers)
    assertEquals(
        listOf(dbs.flight2, dbs.flight3).sortedBy { f -> f.id }, flights.sortedBy { f -> f.id })
  }
}
