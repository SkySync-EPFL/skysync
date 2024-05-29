package ch.epfl.skysync.viewmodel

import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.DateUtility
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.tables.FlightTable
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.FlightStatus
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.location.FlightTrace
import ch.epfl.skysync.models.location.LocationPoint
import ch.epfl.skysync.models.reports.FlightReport
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FlightsViewModelTest {

  private val db = FirestoreDatabase(useEmulator = true)
  private val dbSetup = DatabaseSetup()
  private val flightTable = FlightTable(db)
  private val repository = Repository(db)

  // adding this rule should set the test dispatcher and should
  // enable us to use advanceUntilIdle(), but it seems advanceUntilIdle
  // cancel the coroutine instead of waiting for it to finish
  // instead use the .join() for the moment
  // @ExperimentalCoroutinesApi @get:Rule var mainCoroutineRule = MainCoroutineRule()

  @get:Rule val composeTestRule = createComposeRule()
  lateinit var viewModelAdmin: FlightsViewModel
  lateinit var viewModelCrewPilot: FlightsViewModel

  @Before
  fun setUp() = runTest {
    dbSetup.clearDatabase(db)
    dbSetup.fillDatabase(db)
  }

  @Test
  fun flightStatusOfFinishedFlightIsUpdatedForAdmin() = runTest {
    var finishedFlight =
        dbSetup.flight4.finishFlight(
            takeOffTime = LocalTime.of(12, 0, 0),
            landingTime = LocalTime.of(14, 0, 0),
            takeOffLocation = LocationPoint(0, 0.1, 0.1, "TakeOffSpot"),
            landingLocation = LocationPoint(0, 0.1, 0.1, "LandingSpot"),
            flightTime = 2,
            flightTrace = FlightTrace(dbSetup.flight4.id, listOf()))
    val report1 =
        FlightReport(
            author = dbSetup.crew1.id,
            begin =
                DateUtility.localDateAndTimeToDate(
                    LocalDate.of(2021, 1, 1),
                    LocalTime.of(12, 0, 0),
                ),
            end =
                DateUtility.localDateAndTimeToDate(
                    LocalDate.of(2021, 1, 1),
                    LocalTime.of(12, 2, 0),
                ),
            pauseDuration = 10,
            comments = "hola",
        )
    val report2 = report1.copy(author = dbSetup.pilot1.id)
    val report3 = report1.copy(author = dbSetup.crew2.id)

    finishedFlight = finishedFlight.copy(reportId = listOf(report1))
    flightTable.update(dbSetup.flight4.id, finishedFlight, onError = { assertNull(it) })
    composeTestRule.setContent {
      viewModelAdmin = FlightsViewModel.createViewModel(repository, dbSetup.admin1.id)
    }
    viewModelAdmin.refreshUserAndFlights().join()
    var currentFlights = viewModelAdmin.currentFlights.value!!
    assertTrue(currentFlights.any { it.id == dbSetup.flight4.id })
    currentFlights.forEach {
      if (it.id == dbSetup.flight4.id) {
        assertEquals(it.getFlightStatus(), FlightStatus.MISSING_REPORT)
      }
    }
    repository.reportTable.addAll(listOf(report2, report3), finishedFlight.id)
    val reports = repository.reportTable.retrieveReports(finishedFlight.id)
    assertEquals(3, reports.size)
    viewModelAdmin.refreshUserAndFlights().join()
    currentFlights = viewModelAdmin.currentFlights.value!!
    assertFalse(currentFlights.any { it.id == dbSetup.flight4.id })
  }

  @Test
  fun flightStatusOfFinishedFlightIsUpdatedForCrew() = runTest {
    var finishedFlight =
        dbSetup.flight4.finishFlight(
            takeOffTime = LocalTime.of(12, 0, 0),
            landingTime = LocalTime.of(14, 0, 0),
            takeOffLocation = LocationPoint(0, 0.1, 0.1, "TakeOffSpot"),
            landingLocation = LocationPoint(0, 0.1, 0.1, "LandingSpot"),
            flightTime = 2,
            flightTrace = FlightTrace(dbSetup.flight4.id, listOf()))
    val report1 =
        FlightReport(
            author = dbSetup.crew1.id,
            begin =
                DateUtility.localDateAndTimeToDate(
                    LocalDate.of(2021, 1, 1),
                    LocalTime.of(12, 0, 0),
                ),
            end =
                DateUtility.localDateAndTimeToDate(
                    LocalDate.of(2021, 1, 1),
                    LocalTime.of(12, 2, 0),
                ),
            pauseDuration = 10,
            comments = "hola",
        )
    val report2 = report1.copy(author = dbSetup.pilot1.id)

    finishedFlight = finishedFlight.copy(reportId = listOf(report1))
    flightTable.update(dbSetup.flight4.id, finishedFlight, onError = { assertNull(it) })
    composeTestRule.setContent {
      viewModelCrewPilot = FlightsViewModel.createViewModel(repository, dbSetup.pilot1.id)
    }
    viewModelCrewPilot.refreshUserAndFlights().join()
    var currentFlights = viewModelCrewPilot.currentFlights.value!!
    assertTrue(currentFlights.any { it.id == dbSetup.flight4.id })
    currentFlights.forEach {
      if (it.id == dbSetup.flight4.id) {
        assertEquals(it.getFlightStatus(), FlightStatus.MISSING_REPORT)
      }
    }
    repository.reportTable.add(report2, finishedFlight.id)
    viewModelCrewPilot.refreshUserAndFlights().join()
    currentFlights = viewModelCrewPilot.currentFlights.value!!
    assertTrue(currentFlights.none { it.id == dbSetup.flight4.id })
  }

  @Test
  fun setDate() {
    composeTestRule.setContent {
      viewModelAdmin = FlightsViewModel.createViewModel(repository, dbSetup.admin1.id)
    }
    viewModelAdmin.setDateAndTimeSlot(LocalDate.of(2024, 8, 12), TimeSlot.AM)
    assertEquals(LocalDate.of(2024, 8, 12), viewModelAdmin.date)
    assertEquals(TimeSlot.AM, viewModelAdmin.timeSlot)
  }

  @Test
  fun loadsCorrectAdmin() {
    composeTestRule.setContent {
      viewModelAdmin = FlightsViewModel.createViewModel(repository, dbSetup.admin1.id)
    }
    runTest {
      viewModelAdmin.refreshUserAndFlights().join()
      val currentUser = viewModelAdmin.currentUser.value
      assertEquals(dbSetup.admin1.id, currentUser?.id)
    }
  }

  @Test
  fun loadsCorrectCrew() {
    composeTestRule.setContent {
      viewModelAdmin = FlightsViewModel.createViewModel(repository, dbSetup.crew1.id)
    }
    runTest {
      viewModelAdmin.refreshUserAndFlights().join()
      val currentUser = viewModelAdmin.currentUser.value
      assertEquals(dbSetup.crew1.id, currentUser?.id)
    }
  }

  @Test
  fun fetchesCurrentFlightsOnInitForAdmin() {
    composeTestRule.setContent {
      viewModelAdmin = FlightsViewModel.createViewModel(repository, dbSetup.admin1.id)
    }
    runTest {
      viewModelAdmin.refreshUserAndFlights().join()
      val currentFlights = viewModelAdmin.currentFlights.value
      assertEquals(5, currentFlights?.size)
    }
  }

  @Test
  fun fetchesCurrentFlightsIfAffectedAsCrew() {
    composeTestRule.setContent {
      viewModelCrewPilot = FlightsViewModel.createViewModel(repository, dbSetup.crew1.id)
    }
    runTest {
      viewModelCrewPilot.refreshUserAndFlights().join()
      val currentFlights = viewModelCrewPilot.currentFlights.value
      assertEquals(3, currentFlights?.size)
    }
  }

  @Test
  fun doesNotFetchCurrentFlightsIfNotAffected() {
    composeTestRule.setContent {
      viewModelCrewPilot = FlightsViewModel.createViewModel(repository, dbSetup.pilot2.id)
    }
    runTest {
      viewModelCrewPilot.refreshUserAndFlights().join()
      val currentFlights = viewModelCrewPilot.currentFlights.value
      assertEquals(1, currentFlights?.size)
    }
  }

  @Test
  fun fetchesRelevantCurrentFlightsAsCrew() {
    composeTestRule.setContent {
      viewModelCrewPilot = FlightsViewModel.createViewModel(repository, dbSetup.crew1.id)
    }
    runTest() {
      var flightWithCrew =
          PlannedFlight(
              nPassengers = 3,
              team =
                  Team(
                      roles =
                          listOf(
                              Role(RoleType.PILOT, dbSetup.pilot1),
                              Role(RoleType.CREW, dbSetup.crew1))),
              flightType = dbSetup.flightType1,
              balloon = dbSetup.balloon1,
              basket = dbSetup.basket2,
              date = LocalDate.of(2024, 8, 12),
              timeSlot = TimeSlot.AM,
              vehicles = listOf(dbSetup.vehicle1),
              id = UNSET_ID)

      var flightWithoutCrew =
          PlannedFlight(
              nPassengers = 4,
              team =
                  Team(
                      roles =
                          listOf(
                              Role(RoleType.PILOT, dbSetup.pilot1),
                          )),
              flightType = dbSetup.flightType2,
              balloon = dbSetup.balloon1,
              basket = dbSetup.basket2,
              date = LocalDate.of(2024, 8, 12),
              timeSlot = TimeSlot.PM,
              vehicles = listOf(dbSetup.vehicle1),
              id = UNSET_ID)

      flightWithCrew =
          flightWithCrew.copy(id = flightTable.add(flightWithCrew, onError = { assertNull(it) }))

      flightWithoutCrew =
          flightWithoutCrew.copy(
              id = flightTable.add(flightWithoutCrew, onError = { assertNull(it) }))

      viewModelCrewPilot.refreshUserAndFlights().join()
      assertEquals(4, viewModelCrewPilot.currentFlights.value?.size)
    }
  }

  @Test
  fun fetchesAllCurrentFlightsAsAdmin() {
    composeTestRule.setContent {
      viewModelAdmin = FlightsViewModel.createViewModel(repository, dbSetup.admin1.id)
    }
    runTest() {
      var flightWithCrew =
          PlannedFlight(
              nPassengers = 3,
              team =
                  Team(
                      roles =
                          listOf(
                              Role(RoleType.PILOT, dbSetup.pilot1),
                              Role(RoleType.CREW, dbSetup.crew1))),
              flightType = dbSetup.flightType1,
              balloon = dbSetup.balloon1,
              basket = dbSetup.basket2,
              date = LocalDate.of(2024, 8, 12),
              timeSlot = TimeSlot.AM,
              vehicles = listOf(dbSetup.vehicle1),
              id = UNSET_ID)

      var flightWithoutCrew =
          PlannedFlight(
              nPassengers = 4,
              team =
                  Team(
                      roles =
                          listOf(
                              Role(RoleType.PILOT, dbSetup.pilot1),
                          )),
              flightType = dbSetup.flightType2,
              balloon = dbSetup.balloon1,
              basket = dbSetup.basket2,
              date = LocalDate.of(2024, 8, 12),
              timeSlot = TimeSlot.PM,
              vehicles = listOf(dbSetup.vehicle1),
              id = UNSET_ID)

      flightWithCrew =
          flightWithCrew.copy(id = flightTable.add(flightWithCrew, onError = { assertNull(it) }))

      flightWithoutCrew =
          flightWithoutCrew.copy(
              id = flightTable.add(flightWithoutCrew, onError = { assertNull(it) }))

      viewModelAdmin.refreshUserAndFlights().join()
      assertEquals(7, viewModelAdmin.currentFlights.value?.size)
    }
  }

  @Test
  fun addsFlight() {
    composeTestRule.setContent {
      viewModelAdmin = FlightsViewModel.createViewModel(repository, dbSetup.admin1.id)
    }

    runTest {
      val flight1 =
          PlannedFlight(
              nPassengers = 2,
              team =
                  Team(
                      roles =
                          listOf(
                              Role(RoleType.PILOT, dbSetup.pilot1),
                              Role(RoleType.CREW, dbSetup.crew1))),
              flightType = dbSetup.flightType2,
              balloon = dbSetup.balloon1,
              basket = dbSetup.basket2,
              date = dbSetup.date2,
              timeSlot = dbSetup.date2TimeSlot,
              vehicles = listOf(dbSetup.vehicle1),
              id = UNSET_ID)

      viewModelAdmin.addFlight(flight1).join()

      viewModelAdmin.refreshUserAndFlights().join()

      assertEquals(6, viewModelAdmin.currentFlights.value?.size)
    }
  }

  @Test
  fun deletesFlight() {
    composeTestRule.setContent {
      viewModelAdmin = FlightsViewModel.createViewModel(repository, dbSetup.admin1.id)
    }

    runTest {
      var flight1 =
          PlannedFlight(
              nPassengers = 2,
              team =
                  Team(
                      roles =
                          listOf(
                              Role(RoleType.PILOT, dbSetup.pilot1),
                              Role(RoleType.CREW, dbSetup.crew1))),
              flightType = dbSetup.flightType2,
              balloon = dbSetup.balloon1,
              basket = dbSetup.basket2,
              date = dbSetup.date2,
              timeSlot = dbSetup.date2TimeSlot,
              vehicles = listOf(dbSetup.vehicle1),
              id = UNSET_ID)

      var flight2 =
          PlannedFlight(
              nPassengers = 2,
              team =
                  Team(
                      roles =
                          listOf(
                              Role(RoleType.PILOT, dbSetup.pilot1),
                              Role(RoleType.CREW, dbSetup.crew1))),
              flightType = dbSetup.flightType1,
              balloon = dbSetup.balloon1,
              basket = dbSetup.basket2,
              date = LocalDate.of(2024, 8, 12),
              timeSlot = TimeSlot.AM,
              vehicles = listOf(dbSetup.vehicle1),
              id = UNSET_ID)
      viewModelAdmin.refreshUserAndFlights().join()
      val initFlights = viewModelAdmin.currentFlights.value
      assertEquals(5, initFlights?.size)

      flight1 = flight1.copy(id = flightTable.add(flight1, onError = { assertNull(it) }))

      flight2 = flight2.copy(id = flightTable.add(flight2, onError = { assertNull(it) }))

      viewModelAdmin.refreshUserAndFlights().join()
      val withFlightsAdded = viewModelAdmin.currentFlights.value

      assertEquals(7, withFlightsAdded?.size)

      viewModelAdmin.deleteFlight(flight1).join()

      viewModelAdmin.refreshUserAndFlights().join()

      val withOneFlightDeleted = viewModelAdmin.currentFlights.value

      assertEquals(6, withOneFlightDeleted?.size)
      assertTrue(withOneFlightDeleted?.contains(flight2) ?: false)
      assertFalse(withOneFlightDeleted?.contains(flight1) ?: true)
    }
  }

  @Test
  fun modifyFlight() {
    composeTestRule.setContent {
      viewModelAdmin = FlightsViewModel.createViewModel(repository, dbSetup.admin1.id)
    }

    runTest {
      var flight1 =
          PlannedFlight(
              nPassengers = 2,
              team =
                  Team(
                      roles =
                          listOf(
                              Role(RoleType.PILOT, dbSetup.pilot1),
                              Role(RoleType.CREW, dbSetup.crew1))),
              flightType = dbSetup.flightType2,
              balloon = dbSetup.balloon1,
              basket = dbSetup.basket2,
              date = dbSetup.date2,
              timeSlot = dbSetup.date2TimeSlot,
              vehicles = listOf(dbSetup.vehicle1),
              id = UNSET_ID)

      flight1 = flight1.copy(id = flightTable.add(flight1, onError = { assertNull(it) }))

      // we first need to refresh as otherwise the view model doesn't know about the flight
      // also it doesn't make sense to modify a flight you didn't load in the first place
      viewModelAdmin.refreshUserAndFlights().join()

      val modifiedFlight = flight1.copy(nPassengers = 3)

      viewModelAdmin.modifyFlight(modifiedFlight).join()

      viewModelAdmin.getFlight("dummy")

      viewModelAdmin.refreshUserAndFlights().join()

      assertEquals(6, viewModelAdmin.currentFlights.value?.size)
      assertTrue(viewModelAdmin.currentFlights.value?.contains(modifiedFlight) ?: false)
    }
  }

  @Test
  fun hasOnlyAvailableEquipment() {
    composeTestRule.setContent {
      viewModelAdmin = FlightsViewModel.createViewModel(repository, dbSetup.admin1.id)
    }
    runTest {
      viewModelAdmin.refreshUserAndFlights().join()
      viewModelAdmin.setDateAndTimeSlot(dbSetup.date2, dbSetup.date2TimeSlot)
      viewModelAdmin.refreshUserAndFlights().join()
      val availableBalloons = viewModelAdmin.currentBalloons.value
      val expectedAvailableBalloons = listOf(dbSetup.balloon2, dbSetup.balloon3)
      expectedAvailableBalloons.forEach() { assertTrue(availableBalloons.contains(it)) }
      assertEquals(expectedAvailableBalloons.size, availableBalloons.size)
      val availableBaskets = viewModelAdmin.currentBaskets.value
      val expectedAvailableBaskets = listOf(dbSetup.basket2, dbSetup.basket3)
      expectedAvailableBaskets.forEach() { assertTrue(availableBaskets.contains(it)) }
      assertEquals(expectedAvailableBaskets.size, availableBaskets.size)

      val availableVehicles = viewModelAdmin.currentVehicles.value
      val exepctedAvailableVehicles = listOf(dbSetup.vehicle1, dbSetup.vehicle3)
      exepctedAvailableVehicles.forEach() { assertTrue(availableVehicles.contains(it)) }
      assertEquals(exepctedAvailableVehicles.size, availableVehicles.size)
    }
  }

  @Test
  fun hasOnlyAvailableUser() {
    composeTestRule.setContent {
      viewModelAdmin = FlightsViewModel.createViewModel(repository, dbSetup.admin1.id)
    }
    runTest {
      viewModelAdmin.refreshUserAndFlights().join()
      viewModelAdmin.setDateAndTimeSlot(dbSetup.date2, dbSetup.date2TimeSlotInverse)
      viewModelAdmin.refreshUserAndFlights().join()
      val foundAvailableUsers = viewModelAdmin.availableUsers.value
      val expectedAvailableUsers =
          listOf(dbSetup.admin1, dbSetup.crew1, dbSetup.crew2, dbSetup.pilot1)
      assertEquals(expectedAvailableUsers.size, foundAvailableUsers.size)
      expectedAvailableUsers.forEach { outerUsr ->
        assertTrue(foundAvailableUsers.any { it.id == outerUsr.id })
      }
    }
  }
}
