package ch.epfl.skysync.viewmodel

import androidx.compose.ui.test.junit4.createComposeRule
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
      calendarViewModel = CalendarViewModel.createViewModel(dbs.crew1.id, Repository(db))
    }
  }

  @Test
  fun testReadingAvailabilityCalendar() = runTest {
    calendarViewModel.refresh().join()
    val availabilityCalendar = calendarViewModel.currentAvailabilityCalendar.value
    assertEquals(
        AvailabilityStatus.OK,
        availabilityCalendar.getAvailabilityStatus(
            dbs.availability1Crew1.date, dbs.availability1Crew1.timeSlot))
    assertEquals(
        AvailabilityStatus.UNDEFINED,
        availabilityCalendar.getAvailabilityStatus(LocalDate.of(1999, 10, 1), TimeSlot.AM))
  }

  @Test
  fun testSetToNextAvailabilityStatusForExisting() = runTest {
    calendarViewModel.refresh().join()
    // init status
    var availabilityCalendar = calendarViewModel.currentAvailabilityCalendar.value
    var currentStatus =
        availabilityCalendar.getAvailabilityStatus(
            dbs.availability1Crew1.date, dbs.availability1Crew1.timeSlot)
    assertEquals(AvailabilityStatus.OK, currentStatus)
    calendarViewModel.setToNextAvailabilityStatus(
        dbs.availability1Crew1.date, dbs.availability1Crew1.timeSlot)
    // 1x next
    availabilityCalendar = calendarViewModel.currentAvailabilityCalendar.value
    currentStatus =
        availabilityCalendar.getAvailabilityStatus(
            dbs.availability1Crew1.date, dbs.availability1Crew1.timeSlot)
    assertEquals(AvailabilityStatus.MAYBE, currentStatus)

    // 2x next
    calendarViewModel.setToNextAvailabilityStatus(
        dbs.availability1Crew1.date, dbs.availability1Crew1.timeSlot)
    calendarViewModel.setToNextAvailabilityStatus(
        dbs.availability1Crew1.date, dbs.availability1Crew1.timeSlot)
    availabilityCalendar = calendarViewModel.currentAvailabilityCalendar.value
    currentStatus =
        availabilityCalendar.getAvailabilityStatus(
            dbs.availability1Crew1.date, dbs.availability1Crew1.timeSlot)
    assertEquals(AvailabilityStatus.UNDEFINED, currentStatus)
  }

  @Test
  fun testSetToNextAvailabilityStatusForNewAvailability() = runTest {
    calendarViewModel.refresh().join()
    var availabilityCalendar = calendarViewModel.currentAvailabilityCalendar.value
    val newDate = LocalDate.of(2024, 8, 11)
    val newSlot = TimeSlot.AM
    var currentStatus = availabilityCalendar.getAvailabilityStatus(newDate, newSlot)
    assertEquals(AvailabilityStatus.UNDEFINED, currentStatus)
    calendarViewModel.setToNextAvailabilityStatus(newDate, newSlot)
    availabilityCalendar = calendarViewModel.currentAvailabilityCalendar.value
    currentStatus = availabilityCalendar.getAvailabilityStatus(newDate, newSlot)
    assertEquals(AvailabilityStatus.OK, currentStatus)
  }

  @Test
  fun testSaveAvailabilities() = runTest {
    var allAvailabilities =
        userTable.retrieveAvailabilities(dbs.crew1.id, onError = { assertNull(it) })
    val newDate = LocalDate.of(2024, 8, 11)
    val newSlot = TimeSlot.AM

    val newAvailabilityIsNotYetPresent =
        allAvailabilities.all { it.date != newDate || it.timeSlot != newSlot }
    assertTrue(newAvailabilityIsNotYetPresent)

    calendarViewModel.refresh().join()
    calendarViewModel.setToNextAvailabilityStatus(newDate, newSlot)
    calendarViewModel.saveAvailabilities().join()

    allAvailabilities = userTable.retrieveAvailabilities(dbs.crew1.id, onError = { assertNull(it) })
    val newAvailabilityIsPresent =
        allAvailabilities.any {
          it.date == newDate && it.timeSlot == newSlot && it.status == AvailabilityStatus.OK
        }
    assertTrue(newAvailabilityIsPresent)
  }

  @Test
  fun testCancelAvailabilities() = runTest {
    calendarViewModel.refresh().join()

    var availabilityCalendar = calendarViewModel.currentAvailabilityCalendar.value

    val newDate = LocalDate.of(2024, 8, 11)
    val newTimeSlot = TimeSlot.AM

    assertEquals(
        AvailabilityStatus.OK,
        availabilityCalendar.getAvailabilityStatus(
            dbs.availability1Crew1.date, dbs.availability1Crew2.timeSlot))
    assertEquals(
        AvailabilityStatus.UNDEFINED,
        availabilityCalendar.getAvailabilityStatus(newDate, newTimeSlot))

    calendarViewModel.setToNextAvailabilityStatus(
        dbs.availability1Crew1.date, dbs.availability1Crew1.timeSlot)
    calendarViewModel.setToNextAvailabilityStatus(newDate, newTimeSlot)

    availabilityCalendar = calendarViewModel.currentAvailabilityCalendar.value
    assertEquals(
        AvailabilityStatus.MAYBE,
        availabilityCalendar.getAvailabilityStatus(
            dbs.availability1Crew1.date, dbs.availability1Crew2.timeSlot))
    assertEquals(
        AvailabilityStatus.OK, availabilityCalendar.getAvailabilityStatus(newDate, newTimeSlot))

    calendarViewModel.cancelAvailabilities()
    availabilityCalendar = calendarViewModel.currentAvailabilityCalendar.value

    assertEquals(
        AvailabilityStatus.OK,
        availabilityCalendar.getAvailabilityStatus(
            dbs.availability1Crew1.date, dbs.availability1Crew2.timeSlot))
    assertEquals(
        AvailabilityStatus.UNDEFINED,
        availabilityCalendar.getAvailabilityStatus(newDate, newTimeSlot))
  }
}
