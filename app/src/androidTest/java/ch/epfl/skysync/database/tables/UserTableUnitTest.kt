package ch.epfl.skysync.database.tables

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.user.User
import com.google.firebase.firestore.Filter
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserTableUnitTest {
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  private val userTable = UserTable(db)
  private val availabilityTable = AvailabilityTable(db)
  private val flightTable = FlightTable(db)
  private val flightMemberTable = FlightMemberTable(db)

  @Before
  fun testSetup() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
  }

  @Test
  fun getTest() = runTest {
    var user = userTable.get(dbs.admin1.id, onError = { assertNull(it) })
    assertEquals(dbs.admin1, user)

    user = userTable.get(dbs.admin2.id, onError = { assertNull(it) })
    assertEquals(dbs.admin2, user)

    user = userTable.get(dbs.crew1.id, onError = { assertNull(it) })
    assertEquals(dbs.crew1, user)

    user = userTable.get(dbs.pilot1.id, onError = { assertNull(it) })
    assertEquals(dbs.pilot1, user)
  }

  @Test
  fun queryTest() = runTest {
    val users = userTable.query(Filter.equalTo("lastname", "Bob"), onError = { assertNull(it) })
    assertTrue(listOf(dbs.crew1, dbs.pilot1).containsAll(users))
  }

  @Test
  fun updateTest() = runTest {
    val newAdmin2 = dbs.admin2.copy(firstname = "new-admin-2")

    userTable.update(newAdmin2.id, newAdmin2, onError = { assertNull(it) })

    val user = userTable.get(newAdmin2.id, onError = { assertNull(it) })

    assertEquals(newAdmin2, user)
  }

  @Test
  fun deleteTest() = runTest {
    userTable.delete(dbs.crew1.id, onError = { assertNull(it) })

    val availabilities = availabilityTable.getAll(onError = { assertNull(it) })

    assertTrue(
        listOf(
                dbs.availability1Crew1,
                dbs.availability2Crew1,
                dbs.availability3Crew1,
                dbs.availability1Crew2,
                dbs.availability2Crew2,
                dbs.availability3Crew2,
                dbs.availability1Pilot1,
                dbs.availability2Pilot1,
                dbs.availability3Pilot1,
                dbs.availability1Pilot2,
                dbs.availability2Pilot2,
                dbs.availability3Pilot2,
                dbs.availability1Admin1,
                dbs.availability2Admin1)
            .containsAll(availabilities))

    val flightMembers =
        flightMemberTable.query(
            Filter.equalTo("flightId", dbs.flight1.id), onError = { assertNull(it) })

    assertEquals(2, flightMembers.size)
    assertEquals(dbs.pilot1.id, flightMembers.find { it.roleType == RoleType.PILOT }?.userId)
    assertEquals(null, flightMembers.find { it.roleType == RoleType.CREW }?.userId)

    val user = userTable.get(dbs.crew1.id, onError = { assertNull(it) })

    assertEquals(null, user)
  }

  @Test
  fun retrieveAssignedFlightsTest() = runTest {
    val flights =
        userTable.retrieveAssignedFlights(flightTable, dbs.pilot1.id, onError = { assertNull(it) })
    assertTrue(flights.contains(dbs.flight1))
  }

  @Test
  fun getUsersAvailableOnTest() = runTest {
    var availableUsers =
        userTable.getUsersAvailableOn(
            flightTable = flightTable, dbs.date1, TimeSlot.AM, onError = { assertNull(it) })

    assertEquals(listOf<User>(), availableUsers)

    availableUsers =
        userTable.getUsersAvailableOn(
            flightTable = flightTable, dbs.date1, TimeSlot.PM, onError = { assertNull(it) })

    assertEquals(listOf(dbs.crew2), availableUsers)

    availableUsers =
        userTable.getUsersAvailableOn(
            flightTable = flightTable, dbs.dateNoFlight, TimeSlot.AM, onError = { assertNull(it) })

    assertEquals(listOf(dbs.pilot2), availableUsers)
  }
}
