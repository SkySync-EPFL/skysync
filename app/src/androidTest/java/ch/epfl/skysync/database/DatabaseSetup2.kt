package ch.epfl.skysync.database

import android.os.SystemClock
import android.util.Log
import ch.epfl.skysync.database.tables.AvailabilityTable
import ch.epfl.skysync.database.tables.BalloonTable
import ch.epfl.skysync.database.tables.BasketTable
import ch.epfl.skysync.database.tables.DB_SLEEP_TIME
import ch.epfl.skysync.database.tables.FlightMemberTable
import ch.epfl.skysync.database.tables.FlightTable
import ch.epfl.skysync.database.tables.FlightTypeTable
import ch.epfl.skysync.database.tables.UserTable
import ch.epfl.skysync.database.tables.VehicleTable
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.Availability
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.user.Admin
import ch.epfl.skysync.models.user.Crew
import ch.epfl.skysync.models.user.Pilot
import java.time.LocalDate




/**
 * Represent a mock database setup
 *
 * Define sample data and a standard database state ([fillDatabase]). Designed to be used on a blank
 * database, use [clearDatabase] to clear any existing data.
 */
class DatabaseSetup2 {
    val LOG_TAG = "RemoteDBsetup"
  val admin1 =
      Admin(
          firstname = "admin-1",
          lastname = "lastname",
          availabilities = AvailabilityCalendar(),
          assignedFlights = FlightGroupCalendar())
  val admin2 =
      Admin(
          firstname = "admin-2",
          lastname = "lastname",
          availabilities = AvailabilityCalendar(),
          assignedFlights = FlightGroupCalendar())
  val crew1 =
      Crew(
          firstname = "crew-1",
          lastname = "lastname",
          availabilities = AvailabilityCalendar(),
          assignedFlights = FlightGroupCalendar())
  val pilot1 =
      Pilot(
          firstname = "pilot-1",
          lastname = "lastname",
          availabilities = AvailabilityCalendar(),
          assignedFlights = FlightGroupCalendar(),
          qualification = BalloonQualification.LARGE)

    val allStaff = listOf(admin1, admin2, crew1, pilot1)

  val availability1 =
      Availability(
          status = AvailabilityStatus.MAYBE,
          timeSlot = TimeSlot.AM,
          date = LocalDate.of(2024, 8, 12))
  val availability2 =
      Availability(
          status = AvailabilityStatus.MAYBE,
          timeSlot = TimeSlot.AM,
          date = LocalDate.of(2024, 8, 12))
  val availability3 =
      Availability(
          status = AvailabilityStatus.NO, timeSlot = TimeSlot.AM, date = LocalDate.of(2024, 8, 14))
  val availability4 =
      Availability(
          status = AvailabilityStatus.OK, timeSlot = TimeSlot.PM, date = LocalDate.of(2024, 8, 15))

    val allAvailabilities = listOf(availability1, availability2, availability3, availability4)



  val balloon1 = Balloon(name = "balloon-1", qualification = BalloonQualification.MEDIUM)

  val balloon2 = Balloon(name = "balloon-2", qualification = BalloonQualification.LARGE)

    val allBalloons = listOf(balloon1, balloon2)

  val basket1 = Basket(name = "basket-1", hasDoor = false)

  val basket2 = Basket(name = "basket-2", hasDoor = true)

    val allBaskets = listOf(basket1, basket2)

  val flightType1 = FlightType.DISCOVERY
  val flightType2 = FlightType.FONDUE

    val allFlightTypes = listOf(flightType1, flightType2)

  val vehicle1 = Vehicle(name = "vehicle-1")
  val vehicle2 = Vehicle(name = "vehicle-2")

    val allVehicles = listOf(vehicle1, vehicle2)

  val flight1 =
      PlannedFlight(
          nPassengers = 2,
          team = Team(roles = listOf(Role(RoleType.PILOT, pilot1), Role(RoleType.CREW, crew1))),
          flightType = flightType1,
          balloon = balloon1,
          basket = basket1,
          date = LocalDate.of(2024, 8, 12),
          timeSlot = TimeSlot.AM,
          vehicles = listOf(vehicle1),
          id = UNSET_ID)

    val flight2 =
        PlannedFlight(
            nPassengers = 2,
            team = Team(roles = listOf(Role(RoleType.PILOT, pilot1), Role(RoleType.CREW, crew1))),
            flightType = flightType2,
            balloon = balloon2,
            basket = basket2,
            date = LocalDate.of(2024, 8, 14),
            timeSlot = TimeSlot.PM,
            vehicles = listOf(vehicle2, vehicle1),
            id = UNSET_ID)

    val allFlights = listOf(flight1, flight2)

  /**
   * Delete all items in all tables of the database
   *
   * @param db Firestore database instance
   */
  fun clearDatabase(db: FirestoreDatabase) {
    FlightTypeTable(db).deleteTable {}
    BalloonTable(db).deleteTable {}
    BasketTable(db).deleteTable {}
    VehicleTable(db).deleteTable {}
    FlightMemberTable(db).deleteTable {}
    UserTable(db).deleteTable {}
    FlightTable(db).deleteTable {}
    AvailabilityTable(db).deleteTable {}

    SystemClock.sleep(DB_SLEEP_TIME)
  }

  /**
   * Fill the database with a standard state
   *
   * @param db Firestore database instance
   */
  fun fillDatabase(db: FirestoreDatabase) {
    val flightTypeTable = FlightTypeTable(db)
    val balloonTable = BalloonTable(db)
    val basketTable = BasketTable(db)
    val vehicleTable = VehicleTable(db)
    val userTable = UserTable(db)
    val flightTable = FlightTable(db)

      allStaff.forEach() {
          userTable.add(it, {}, {printError(it)})
      }

      allFlightTypes.forEach() {
          flightTypeTable.add(it, {}, {printError(it)})
      }

      allBalloons.forEach() {
          balloonTable.add(it, {}, {printError(it)})
      }

      allBaskets.forEach() {
          basketTable.add(it, {}, {printError(it)})
      }

      allVehicles.forEach() {
          vehicleTable.add(it, {}, {printError(it)})
      }

      allFlights.forEach() {
          flightTable.add(it, {}, { printError(it)})
      }

  }
    fun printError(Exception: Exception) {
        Log.e(LOG_TAG, Exception.toString())
    }
}

fun main(args: Array<String>) {
    val dbSetup = DatabaseSetup2()
    val db = FirestoreDatabase(useEmulator = false)
    dbSetup.clearDatabase(db)
    dbSetup.fillDatabase(db)
}
