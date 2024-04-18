package ch.epfl.skysync.database.tables

import android.os.SystemClock
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.schemas.FlightMemberSchema
import ch.epfl.skysync.models.calendar.Availability
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.user.User
import com.google.firebase.firestore.Filter
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
  fun testSetup() {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
  }

  @Test
  fun getTest() {
    var user: User? = null
    var isComplete = false
    var isError = false

    userTable.get(
        dbs.admin1.id,
        {
          user = it
          isComplete = true
        },
        { isError = true })

    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, isComplete)
    assertEquals(false, isError)
    assertEquals(dbs.admin1, user)

    user = null
    isComplete = false
    isError = false

    userTable.get(
        dbs.admin2.id,
        {
          user = it
          isComplete = true
        },
        { isError = true })

    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, isComplete)
    assertEquals(false, isError)
    assertEquals(dbs.admin2, user)

    user = null
    isComplete = false
    isError = false

    userTable.get(
        dbs.crew1.id,
        {
          user = it
          isComplete = true
        },
        { isError = true })

    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, isComplete)
    assertEquals(false, isError)
    assertEquals(dbs.crew1, user)
  }

  @Test
  fun queryTest() {
    var users = listOf<User>()
    var isComplete = false
    var isError = false

    userTable.query(
        Filter.equalTo("lastname", "Bob"),
        {
          users = it
          isComplete = true
        },
        { isError = true })

    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, isComplete)
    assertEquals(false, isError)
    assertTrue(listOf(dbs.crew1, dbs.pilot1).containsAll(users))
  }

  @Test
  fun updateTest() {
    var isComplete = false
    var isError = false
    var user: User? = null

    val newAdmin2 = dbs.admin2.copy(firstname = "new-admin-2")

    userTable.update(newAdmin2.id, newAdmin2, { isComplete = true }, { isError = false })

    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, isComplete)
    assertEquals(false, isError)

    isComplete = false
    isError = false

    userTable.get(
        newAdmin2.id,
        {
          user = it
          isComplete = true
        },
        { isError = true })

    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, isComplete)
    assertEquals(false, isError)
    assertEquals(newAdmin2, user)
  }

  @Test
  fun deleteTest() {
    var isComplete = false
    var isError = false

    userTable.delete(dbs.crew1.id, { isComplete = true }, { isError = true })

    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, isComplete)
    assertEquals(false, isError)

    var availabilities = listOf<Availability>()
    isComplete = false
    isError = false

    availabilityTable.getAll(
        {
          availabilities = it
          isComplete = true
        },
        { isError = true })
    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, isComplete)
    assertEquals(false, isError)

    assertTrue(
        listOf(dbs.availability2, dbs.availability3, dbs.availability4, dbs.availability5)
            .containsAll(availabilities))

    var flightMembers = listOf<FlightMemberSchema>()
    isComplete = false
    isError = false

    flightMemberTable.query(
        Filter.equalTo("flightId", dbs.flight1.id),
        {
          flightMembers = it
          isComplete = true
        },
        { isError = false })

    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, isComplete)
    assertEquals(false, isError)
    assertEquals(2, flightMembers.size)
    assertEquals(dbs.pilot1.id, flightMembers.find { it.roleType == RoleType.PILOT }?.userId)
    assertEquals(null, flightMembers.find { it.roleType == RoleType.CREW }?.userId)

    var user: User? = null
    isComplete = false
    isError = false

    userTable.get(
        dbs.crew1.id,
        {
          user = it
          isComplete = true
        },
        { isError = true })

    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, isComplete)
    assertEquals(false, isError)
    assertEquals(null, user)
  }

  @Test
  fun retrieveAssignedFlightsTest() {
    var flights: List<Flight>? = null
    var isComplete = false
    var isError = false

    userTable.retrieveAssignedFlights(
        flightTable,
        dbs.pilot1.id,
        {
          flights = it
          isComplete = true
        },
        { isError = true })

    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, isComplete)
    assertEquals(false, isError)
    assertTrue(flights?.contains(dbs.flight1) ?: false)
  }
}
