package ch.epfl.skysync.viewmodel

import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.tables.BalloonTable
import ch.epfl.skysync.database.tables.BasketTable
import ch.epfl.skysync.database.tables.FlightTable
import ch.epfl.skysync.database.tables.FlightTypeTable
import ch.epfl.skysync.database.tables.VehicleTable
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Flight
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

  // adding this rule should set the test dispatcher and should
  // enable us to use advanceUntilIdle(), but it seems advanceUntilIdle
  // cancel the coroutine instead of waiting for it to finish
  // instead use the .join() for the moment
  // @ExperimentalCoroutinesApi @get:Rule var mainCoroutineRule = MainCoroutineRule()

  @get:Rule val composeTestRule = createComposeRule()
  lateinit var viewModel: FlightsViewModel

  @Before
  fun setUp() = runTest {
  fun setUp() {
      defaultFlight1 = PlannedFlight(
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
    composeTestRule.setContent {
      viewModel =
          FlightsViewModel.createViewModel(
              flightTable, balloonTable, basketTable, flightTypeTable, vehicleTable)
    }
  }

  @Test
  fun fetchesCurrentFlightsIfEmpty() {
    val currentFlights = viewModel.currentFlights.value
    assertEquals(0, currentFlights.size)
  }

  @Test
  fun fetchesCurrentFlights() =
      runTest() {
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

        flight1 = flight1.copy(id = flightTable.add(flight1, onError = { assertNull(it) }))

        flight2 = flight2.copy(id = flightTable.add(flight2, onError = { assertNull(it) }))

        viewModel.refreshCurrentFlights().join()

        assertEquals(3, viewModel.currentFlights.value.size)
      }

  @Test
  fun addsFlight() = runTest {
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

    viewModel.addFlight(flight1).join()

    viewModel.refreshCurrentFlights().join()

    assertEquals(2, viewModel.currentFlights.value.size)
  }

  @Test
  fun deletesFlight() = runTest {
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

    flight1 = flight1.copy(id = flightTable.add(flight1, onError = { assertNull(it) }))

    flight2 = flight2.copy(id = flightTable.add(flight2, onError = { assertNull(it) }))

    viewModel.refreshCurrentFlights().join()

    viewModel.deleteFlight(flight1.id).join()

    assertEquals(2, viewModel.currentFlights.value.size)
    assertTrue(viewModel.currentFlights.value.contains(flight2))
    assertFalse(viewModel.currentFlights.value.contains(flight1))
  }

  @Test
  fun modifyFlight() = runTest {
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
    viewModel.refreshCurrentFlights().join()

    val modifiedFlight = flight1.copy(nPassengers = 3)

    viewModel.modifyFlight(modifiedFlight).join()

    assertEquals(2, viewModel.currentFlights.value.size)
    assertTrue(viewModel.currentFlights.value.contains(modifiedFlight))
  }

  @Test
  fun testGetFlight(){
      var persistedFlight : PlannedFlight? = null
        flightTable.add(defaultFlight1,
            { persistedFlight = defaultFlight1.copy(it)},
            {})
      SystemClock.sleep(DB_SLEEP_TIME)
      viewModel.refreshCurrentFlights()
      SystemClock.sleep(DB_SLEEP_TIME)
      val foundFlight  = viewModel.getFlight(persistedFlight?.id?: "noid")
      Assert.assertEquals(persistedFlight, foundFlight.value)


  }
}
