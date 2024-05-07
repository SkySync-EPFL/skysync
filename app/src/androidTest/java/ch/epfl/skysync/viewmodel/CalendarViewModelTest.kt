package ch.epfl.skysync.viewmodel

import androidx.compose.material.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.tables.AvailabilityTable
import ch.epfl.skysync.database.tables.UserTable
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.TimeSlot
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
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
  fun testSetup() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
    composeTestRule.setContent {
      calendarViewModel = CalendarViewModel.createViewModel(dbs.admin1.id, Repository(db))
      val uiState = calendarViewModel.uiState.collectAsStateWithLifecycle()
      Text(text = uiState.value.user?.firstname ?: "Bob")
    }
  }

  @Test
  fun testSaveAvailabilities() = runTest {
    calendarViewModel.refresh().join()

    val availabilityCalendar = calendarViewModel.uiState.value.availabilityCalendar

    assertEquals(
        AvailabilityStatus.NO,
        availabilityCalendar.getAvailabilityStatus(
            dbs.availability1Admin1.date, dbs.availability1Admin1.timeSlot))
    assertEquals(
        AvailabilityStatus.OK,
        availabilityCalendar.getAvailabilityStatus(
            dbs.availability2Admin1.date, dbs.availability2Admin1.timeSlot))

    val newDate = LocalDate.of(2024, 8, 11)

    // create a new availability using nextAvailabilityStatus
    var status = availabilityCalendar.nextAvailabilityStatus(newDate, TimeSlot.AM)

    assertEquals(AvailabilityStatus.OK, status)

    // delete an availability using nextAvailabilityStatus
    status =
        availabilityCalendar.nextAvailabilityStatus(
            dbs.availability1Admin1.date, dbs.availability1Admin1.timeSlot)

    assertEquals(AvailabilityStatus.UNDEFINED, status)

    calendarViewModel.saveAvailabilities().join()

    val user = userTable.get(dbs.admin1.id, onError = { assertNull(it) })

    assertNotNull(user)

    user!!
        .availabilities
        .addCells(userTable.retrieveAvailabilities(dbs.admin1.id, onError = { assertNull(it) }))

    assertEquals(
        AvailabilityStatus.OK, user!!.availabilities.getAvailabilityStatus(newDate, TimeSlot.AM))
    assertEquals(
        AvailabilityStatus.UNDEFINED,
        user!!.availabilities.getAvailabilityStatus(dbs.availability1Admin1.date, TimeSlot.AM))
  }

  @Test
  fun testCancelAvailabilities() = runTest {
    calendarViewModel.refresh().join()

    val availabilityCalendar = calendarViewModel.uiState.value.availabilityCalendar

    assertEquals(
        AvailabilityStatus.NO,
        availabilityCalendar.getAvailabilityStatus(
            dbs.availability1Admin1.date, dbs.availability1Admin1.timeSlot))
    assertEquals(
        AvailabilityStatus.OK,
        availabilityCalendar.getAvailabilityStatus(
            dbs.availability2Admin1.date, dbs.availability2Admin1.timeSlot))

    val newDate = LocalDate.of(2024, 8, 11)

    // create a new availability using nextAvailabilityStatus
    var status = availabilityCalendar.nextAvailabilityStatus(newDate, TimeSlot.AM)

    assertEquals(AvailabilityStatus.OK, status)

    // delete an availability using nextAvailabilityStatus
    status =
        availabilityCalendar.nextAvailabilityStatus(
            dbs.availability1Admin1.date, dbs.availability1Admin1.timeSlot)

    assertEquals(AvailabilityStatus.UNDEFINED, status)

    calendarViewModel.cancelAvailabilities()

    val user = userTable.get(dbs.admin1.id, onError = { assertNull(it) })

    assertNotNull(user)

    assertEquals(
        AvailabilityStatus.NO,
        user!!
            .availabilities
            .getAvailabilityStatus(dbs.availability1Admin1.date, dbs.availability1Admin1.timeSlot))
    assertEquals(
        AvailabilityStatus.OK,
        user!!
            .availabilities
            .getAvailabilityStatus(dbs.availability2Admin1.date, dbs.availability2Admin1.timeSlot))
    assertEquals(
        AvailabilityStatus.UNDEFINED,
        user!!.availabilities.getAvailabilityStatus(newDate, TimeSlot.AM))
  }
}
