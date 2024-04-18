package ch.epfl.skysync.database

import android.os.SystemClock
import ch.epfl.skysync.database.tables.AvailabilityTable
import ch.epfl.skysync.database.tables.BalloonTable
import ch.epfl.skysync.database.tables.BasketTable
import ch.epfl.skysync.database.tables.DB_SLEEP_TIME
import ch.epfl.skysync.database.tables.FlightMemberTable
import ch.epfl.skysync.database.tables.FlightTable
import ch.epfl.skysync.database.tables.FlightTypeTable
import ch.epfl.skysync.database.tables.UserTable
import ch.epfl.skysync.database.tables.VehicleTable
import ch.epfl.skysync.models.calendar.Availability
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.FlightType
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
  var admin1 =
      Admin(
          firstname = "admin-1",
          lastname = "lastname",
          availabilities = AvailabilityCalendar(),
          assignedFlights = FlightGroupCalendar())
  var admin2 =
      Admin(
          firstname = "admin-2",
          lastname = "lastname",
          availabilities = AvailabilityCalendar(),
          assignedFlights = FlightGroupCalendar())
  var crew1 =
      Crew(
          firstname = "crew-1",
          lastname = "lastname",
          availabilities = AvailabilityCalendar(),
          assignedFlights = FlightGroupCalendar())
  var pilot1 =
      Pilot(
          firstname = "pilot-1",
          lastname = "lastname",
          availabilities = AvailabilityCalendar(),
          assignedFlights = FlightGroupCalendar(),
          qualification = BalloonQualification.LARGE)
  var availability1 =
      Availability(
          status = AvailabilityStatus.MAYBE,
          timeSlot = TimeSlot.AM,
          date = LocalDate.of(2024, 8, 12))
  var availability2 =
      Availability(
          status = AvailabilityStatus.MAYBE,
          timeSlot = TimeSlot.AM,
          date = LocalDate.of(2024, 8, 12))
  var availability3 =
      Availability(
          status = AvailabilityStatus.NO, timeSlot = TimeSlot.AM, date = LocalDate.of(2024, 8, 14))
  var availability4 =
      Availability(
          status = AvailabilityStatus.OK, timeSlot = TimeSlot.PM, date = LocalDate.of(2024, 8, 15))

  var balloon1 = Balloon(name = "balloon-1", qualification = BalloonQualification.MEDIUM)

  var balloon2 = Balloon(name = "balloon-2", qualification = BalloonQualification.LARGE)

  var basket1 = Basket(name = "basket-1", hasDoor = false)

  var basket2 = Basket(name = "basket-2", hasDoor = true)

  var flightType1 = FlightType.DISCOVERY
  var flightType2 = FlightType.FONDUE

  var vehicle1 = Vehicle(name = "vehicle-1")
  var vehicle2 = Vehicle(name = "vehicle-2")

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

    flightTypeTable.add(flightType1, { flightType1 = flightType1.copy(id = it) }, {})
    flightTypeTable.add(flightType2, { flightType2 = flightType2.copy(id = it) }, {})

    balloonTable.add(balloon1, { balloon1 = balloon1.copy(id = it) }, {})
    balloonTable.add(balloon2, { balloon2 = balloon2.copy(id = it) }, {})

    basketTable.add(basket1, { basket1 = basket1.copy(id = it) }, {})
    basketTable.add(basket2, { basket2 = basket2.copy(id = it) }, {})

    vehicleTable.add(vehicle1, { vehicle1 = vehicle1.copy(id = it) }, {})
    vehicleTable.add(vehicle2, { vehicle2 = vehicle2.copy(id = it) }, {})

    userTable.add(admin1, { id -> admin1 = admin1.copy(id = id) }, {})
    userTable.add(admin2, { id -> admin2 = admin2.copy(id = id) }, {})
    userTable.add(crew1, { id -> crew1 = crew1.copy(id = id) }, {})
    userTable.add(pilot1, { id -> pilot1 = pilot1.copy(id = id) }, {})

    SystemClock.sleep(DB_SLEEP_TIME)

    // this needs to be done after setting all the IDs

    SystemClock.sleep(DB_SLEEP_TIME)
  }
}
