package ch.epfl.skysync.database.tables

import android.os.SystemClock
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.models.calendar.Availability
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.user.Admin
import ch.epfl.skysync.models.user.Crew
import ch.epfl.skysync.models.user.Pilot
import ch.epfl.skysync.models.user.User
import com.google.firebase.firestore.Filter
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserTableUnitTest {
  private val db = FirestoreDatabase(useEmulator = true)
  private val userTable = UserTable(db)
  private val availabilityTable = AvailabilityTable(db)
  private val customId = "custom-id"
  private var admin1 =
      Admin(
          firstname = "admin-1",
          lastname = "lastname",
          availabilities = AvailabilityCalendar(),
          assignedFlights = FlightGroupCalendar())
  private var admin2 =
      Admin(
          id = customId,
          firstname = "admin-2",
          lastname = "lastname",
          availabilities = AvailabilityCalendar(),
          assignedFlights = FlightGroupCalendar())
  private var crew1 =
      Crew(
          firstname = "crew-1",
          lastname = "bob",
          availabilities = AvailabilityCalendar(),
          assignedFlights = FlightGroupCalendar())
  private var pilot1 =
      Pilot(
          firstname = "pilot-1",
          lastname = "bob",
          availabilities = AvailabilityCalendar(),
          assignedFlights = FlightGroupCalendar(),
          qualification = BalloonQualification.LARGE)
  private var availability1 =
      Availability(
          status = AvailabilityStatus.MAYBE,
          timeSlot = TimeSlot.AM,
          date = LocalDate.now().minusDays(4))
  private var availability2 =
      Availability(
          status = AvailabilityStatus.MAYBE,
          timeSlot = TimeSlot.PM,
          date = LocalDate.now().minusDays(3))
  private var availability3 =
      Availability(
          status = AvailabilityStatus.NO,
          timeSlot = TimeSlot.AM,
          date = LocalDate.now().minusDays(2))
  private var availability4 =
      Availability(
          status = AvailabilityStatus.OK,
          timeSlot = TimeSlot.PM,
          date = LocalDate.now().minusDays(1))
  private var availability5 =
      Availability(
          status = AvailabilityStatus.NO,
          timeSlot = TimeSlot.PM,
          date = LocalDate.now().minusDays(1))

  @Before
  fun testSetup() {
    userTable.deleteTable {}
    availabilityTable.deleteTable {}
    SystemClock.sleep(DB_SLEEP_TIME)

    // setup the initial db state
    userTable.add(
        admin1,
        { id ->
          admin1 = admin1.copy(id = id)
          availabilityTable.add(
              id, availability1, { id -> availability1 = availability1.copy(id = id) }, {})
          availabilityTable.add(
              id, availability2, { id -> availability2 = availability2.copy(id = id) }, {})
        },
        {})

    userTable.set(
        admin2.id,
        admin2,
        {
          availabilityTable.add(
              admin2.id, availability5, { id -> availability5 = availability5.copy(id = id) }, {})
        },
        {})

    userTable.add(
        crew1,
        { id ->
          crew1 = crew1.copy(id = id)
          availabilityTable.add(
              id, availability3, { id -> availability3 = availability3.copy(id = id) }, {})
        },
        {})
    userTable.add(
        pilot1,
        { id ->
          pilot1 = pilot1.copy(id = id)
          availabilityTable.add(
              id, availability4, { id -> availability4 = availability4.copy(id = id) }, {})
        },
        {})
    SystemClock.sleep(DB_SLEEP_TIME)

    // this needs to be done after setting all the IDs
    admin1.availabilities.addCells(listOf(availability1, availability2))
    admin2.availabilities.addCells(listOf(availability5))
    crew1.availabilities.addCells(listOf(availability3))
    pilot1.availabilities.addCells(listOf(availability4))
  }

  @Test
  fun getTest() {
    var user: User? = null
    var isComplete = false
    var isError = false

    userTable.get(
        admin1.id,
        {
          user = it
          isComplete = true
        },
        { isError = true })

    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, isComplete)
    assertEquals(false, isError)
    assertEquals(admin1, user)

    user = null
    isComplete = false
    isError = false

    userTable.get(
        admin2.id,
        {
          user = it
          isComplete = true
        },
        { isError = true })

    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, isComplete)
    assertEquals(false, isError)
    assertEquals(admin2, user)

    user = null
    isComplete = false
    isError = false

    userTable.get(
        crew1.id,
        {
          user = it
          isComplete = true
        },
        { isError = true })

    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, isComplete)
    assertEquals(false, isError)
    assertEquals(crew1, user)
  }

  @Test
  fun queryTest() {
    var users = listOf<User>()
    var isComplete = false
    var isError = false

    userTable.query(
        Filter.equalTo("lastname", "bob"),
        {
          users = it
          isComplete = true
        },
        { isError = true })

    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, isComplete)
    assertEquals(false, isError)
    assertTrue(listOf(crew1, pilot1).containsAll(users))
  }

  @Test
  fun updateTest() {
    var isComplete = false
    var isError = false
    var user: User? = null

    val newAdmin2 = admin2.copy(firstname = "new-admin-2")

    userTable.update(admin2.id, newAdmin2, { isComplete = true }, { isError = false })

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

    userTable.delete(admin1.id, { isComplete = true }, { isError = true })

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
    println(availabilities)
    assertTrue(listOf(availability3, availability4, availability5).containsAll(availabilities))

    var user: User? = null
    isComplete = false
    isError = false

    userTable.get(
        admin1.id,
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
}
