package ch.epfl.skysync.viewmodel

import android.os.SystemClock
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.tables.BalloonTable
import ch.epfl.skysync.database.tables.BasketTable
import ch.epfl.skysync.database.tables.DB_SLEEP_TIME
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
import org.junit.Assert
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

  @get:Rule val composeTestRule = createComposeRule()
  lateinit var viewModel: FlightsViewModel


  lateinit var defaultFlight1: PlannedFlight

  @Before
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
    dbSetup.fillDatabase2(db)
    composeTestRule.setContent {
      viewModel =
          FlightsViewModel.createViewModel(
              flightTable, balloonTable, basketTable, flightTypeTable, vehicleTable)

      // val flights = viewModel.currentFlights.collectAsStateWithLifecycle()
      // Text(flights.value.getOrNull(0)?.toString()?: "No flight types")
    }
  }

  @Test
  fun fetchesCurrentFlightsIfEmpty() {
    val currentFlights = viewModel.currentFlights.value
    Assert.assertEquals(currentFlights.size, 0)
  }

  @Test
  fun fetchesCurrentFlights() {
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

    flightTable.add(flight1, { flight1 = flight1.copy(it) }, { error -> println(error) })

    flightTable.add(flight2, { flight2 = flight2.copy(it) }, { error -> println(error) })

    SystemClock.sleep(DB_SLEEP_TIME)

    viewModel.refreshCurrentFlights()
    SystemClock.sleep(DB_SLEEP_TIME)
    Assert.assertEquals(viewModel.currentFlights.value.size, 2)
  }

  @Test
  fun addsFlight() {
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

    viewModel.addFlight(flight1)
    SystemClock.sleep(DB_SLEEP_TIME)

    viewModel.refreshCurrentFlights()
    SystemClock.sleep(DB_SLEEP_TIME)
    Assert.assertEquals(viewModel.currentFlights.value.size, 1)
  }

  @Test
  fun deletesFlight() {
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

    flightTable.add(flight1, { flight1 = flight1.copy(it) }, { error -> println(error) })

    flightTable.add(flight2, { flight2 = flight2.copy(it) }, { error -> println(error) })
    SystemClock.sleep(DB_SLEEP_TIME)
    viewModel.refreshCurrentFlights()

    SystemClock.sleep(DB_SLEEP_TIME)
    viewModel.deleteFlight(flight1.id)
    SystemClock.sleep(DB_SLEEP_TIME)
    Assert.assertEquals(viewModel.currentFlights.value.size, 1)
    Assert.assertTrue(viewModel.currentFlights.value.contains(flight2))
    Assert.assertFalse(viewModel.currentFlights.value.contains(flight1))
  }

  @Test
  fun modifyFlight() {
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

    flightTable.add(flight1, { flight1 = flight1.copy(it) }, { error -> println(error) })
    SystemClock.sleep(DB_SLEEP_TIME)

    val modifiedFlight = flight1.copy(nPassengers = 3)

    viewModel.modifyFlight(modifiedFlight)
    SystemClock.sleep(DB_SLEEP_TIME)
    Assert.assertEquals(viewModel.currentFlights.value.size, 1)
    Assert.assertEquals(viewModel.currentFlights.value.get(0).nPassengers, 3)
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
