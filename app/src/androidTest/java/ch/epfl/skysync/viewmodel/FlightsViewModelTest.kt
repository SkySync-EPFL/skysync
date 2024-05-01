package ch.epfl.skysync.viewmodel

import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.tables.BalloonTable
import ch.epfl.skysync.database.tables.BasketTable
import ch.epfl.skysync.database.tables.FlightTable
import ch.epfl.skysync.database.tables.FlightTypeTable
import ch.epfl.skysync.database.tables.VehicleTable
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FlightsViewModelTest {

  private val db = FirestoreDatabase(useEmulator = true)
  private val dbSetup = DatabaseSetup()
  private val flightTable = FlightTable(db)
  private val basketTable = BasketTable(db)
  private val balloonTable = BalloonTable(db)
  private val flightTypeTable = FlightTypeTable(db)
  private val vehicleTable = VehicleTable(db)
  private val repository = Repository(db)

  // adding this rule should set the test dispatcher and should
  // enable us to use advanceUntilIdle(), but it seems advanceUntilIdle
  // cancel the coroutine instead of waiting for it to finish
  // instead use the .join() for the moment
  // @ExperimentalCoroutinesApi @get:Rule var mainCoroutineRule = MainCoroutineRule()

  @get:Rule val composeTestRule = createComposeRule()
  lateinit var viewModelAdmin: FlightsViewModel
  lateinit var viewModelCrewPilot: FlightsViewModel
  lateinit var defaultFlight1: PlannedFlight

  @Before
  fun setUp() = runTest {
    defaultFlight1 =
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

    dbSetup.clearDatabase(db)
    dbSetup.fillDatabase(db)
  }

  @Test
  fun loadsCorrectAdmin() {
    composeTestRule.setContent {
      viewModelAdmin = FlightsViewModel.createViewModel(repository, "id-admin-1")
    }
    runTest {
      viewModelAdmin.refreshUserAndFlights().join()
      val currentUser = viewModelAdmin.currentUser.value
      assertEquals("id-admin-1", currentUser?.id)
    }
  }

  @Test
  fun loadsCorrectCrew() {
    composeTestRule.setContent {
      viewModelAdmin = FlightsViewModel.createViewModel(repository, "id-crew-1")
    }
    runTest {
      viewModelAdmin.refreshUserAndFlights().join()
      val currentUser = viewModelAdmin.currentUser.value
      assertEquals("id-crew-1", currentUser?.id)
    }
  }

  @Test
  fun fetchesCurrentFlightsOnInitForAdmin() {
    composeTestRule.setContent {
      viewModelAdmin = FlightsViewModel.createViewModel(repository, "id-admin-1")
    }
    runTest {
      viewModelAdmin.refreshUserAndFlights().join()
      val currentFlights = viewModelAdmin.currentFlights.value
      assertEquals(1, currentFlights?.size)
    }
  }

  @Test
  fun fetchesCurrentFlightsIfAffectedAsCrew() {
    composeTestRule.setContent {
      viewModelCrewPilot = FlightsViewModel.createViewModel(repository, "id-crew-1")
    }
    runTest {
      viewModelCrewPilot.refreshUserAndFlights().join()
      val currentFlights = viewModelCrewPilot.currentFlights.value
      assertEquals(1, currentFlights?.size)
    }
  }

  @Test
  fun doesNotFetchCurrentFlightsIfNotAffected() {
    composeTestRule.setContent {
      viewModelCrewPilot = FlightsViewModel.createViewModel(repository, "id-pilot-2")
    }
    runTest {
      viewModelCrewPilot.refreshUserAndFlights().join()
      val currentFlights = viewModelCrewPilot.currentFlights.value
      assertEquals(0, currentFlights?.size)
    }
  }

  @Test
  fun fetchesRelevantCurrentFlightsAsCrew() {
    composeTestRule.setContent {
      viewModelCrewPilot = FlightsViewModel.createViewModel(repository, "id-crew-1")
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
      assertEquals(2, viewModelCrewPilot.currentFlights.value?.size)
    }
  }

  @Test
  fun fetchesAllCurrentFlightsAsAdmin() {
    composeTestRule.setContent {
      viewModelAdmin = FlightsViewModel.createViewModel(repository, "id-admin-1")
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
      assertEquals(3, viewModelAdmin.currentFlights.value?.size)
    }
  }

  @Test
  fun addsFlight() {
    composeTestRule.setContent {
      viewModelAdmin = FlightsViewModel.createViewModel(repository, "id-admin-1")
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
              date = LocalDate.of(2024, 8, 12),
              timeSlot = TimeSlot.AM,
              vehicles = listOf(dbSetup.vehicle1),
              id = UNSET_ID)

      viewModelAdmin.addFlight(flight1).join()

      viewModelAdmin.refreshUserAndFlights().join()

      assertEquals(2, viewModelAdmin.currentFlights.value?.size)
    }
  }

  @Test
  fun deletesFlight() {
    composeTestRule.setContent {
      viewModelAdmin = FlightsViewModel.createViewModel(repository, "id-admin-1")
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
              date = LocalDate.of(2024, 8, 12),
              timeSlot = TimeSlot.AM,
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
      assertEquals(1, initFlights?.size)

      flight1 = flight1.copy(id = flightTable.add(flight1, onError = { assertNull(it) }))

      flight2 = flight2.copy(id = flightTable.add(flight2, onError = { assertNull(it) }))

      viewModelAdmin.refreshUserAndFlights().join()
      val withFlightsAdded = viewModelAdmin.currentFlights.value

      assertEquals(3, withFlightsAdded?.size)

      viewModelAdmin.deleteFlight(flight1.id).join()

      viewModelAdmin.refreshUserAndFlights().join()

      val withOneFlightDeleted = viewModelAdmin.currentFlights.value

      assertEquals(2, withOneFlightDeleted?.size)
      assertTrue(withOneFlightDeleted?.contains(flight2) ?: false)
      assertFalse(withOneFlightDeleted?.contains(flight1) ?: true)
    }
  }

  @Test
  fun modifyFlight() {
    composeTestRule.setContent {
      viewModelAdmin = FlightsViewModel.createViewModel(repository, "id-admin-1")
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
              date = LocalDate.of(2024, 8, 12),
              timeSlot = TimeSlot.AM,
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

      assertEquals(2, viewModelAdmin.currentFlights.value?.size)
      assertTrue(viewModelAdmin.currentFlights.value?.contains(modifiedFlight) ?: false)
    }
  }
}
