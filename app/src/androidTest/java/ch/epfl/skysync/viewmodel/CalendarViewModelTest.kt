package ch.epfl.skysync.viewmodel

import android.os.SystemClock
import androidx.compose.material.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.tables.AvailabilityTable
import ch.epfl.skysync.database.tables.DB_SLEEP_TIME
import ch.epfl.skysync.database.tables.UserTable
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.user.User
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CalendarViewModelTest {
  @get:Rule val composeTestRule = createComposeRule()
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  private val userTable = UserTable(db)
  private val availabilityTable = AvailabilityTable(db)
  private lateinit var calendarViewModel: CalendarViewModel

  @Before
  fun testSetup() {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
    composeTestRule.setContent {
      calendarViewModel =
          CalendarViewModel.createViewModel(dbs.admin1.id, userTable, availabilityTable)
      val uiState = calendarViewModel.uiState.collectAsStateWithLifecycle()
      Text(text = uiState.value.user?.firstname ?: "Bob")
    }
  }

  @Test
  fun testSaveAvailabilities() {
    SystemClock.sleep(DB_SLEEP_TIME)
    val availabilityCalendar = calendarViewModel.uiState.value.availabilityCalendar

    assertEquals(
        AvailabilityStatus.NO,
        availabilityCalendar.getAvailabilityStatus(
            dbs.availability3.date, dbs.availability3.timeSlot))
    assertEquals(
        AvailabilityStatus.OK,
        availabilityCalendar.getAvailabilityStatus(
            dbs.availability4.date, dbs.availability4.timeSlot))

    val newDate = LocalDate.of(2024, 8, 11)

    // create a new availability using nextAvailabilityStatus
    var status = availabilityCalendar.nextAvailabilityStatus(newDate, TimeSlot.AM)

    assertEquals(AvailabilityStatus.OK, status)

    // delete an availability using nextAvailabilityStatus
    status = availabilityCalendar.nextAvailabilityStatus(dbs.availability3.date, TimeSlot.AM)

    assertEquals(AvailabilityStatus.UNDEFINED, status)

    calendarViewModel.saveAvailabilities()

    SystemClock.sleep(DB_SLEEP_TIME)

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
    assertNotNull(user)

    assertEquals(
        AvailabilityStatus.OK, user!!.availabilities.getAvailabilityStatus(newDate, TimeSlot.AM))
    assertEquals(
        AvailabilityStatus.UNDEFINED,
        user!!.availabilities.getAvailabilityStatus(dbs.availability3.date, TimeSlot.AM))
  }
}
