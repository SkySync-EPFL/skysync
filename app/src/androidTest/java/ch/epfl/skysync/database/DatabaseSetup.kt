package ch.epfl.skysync.database

import ch.epfl.skysync.database.tables.AvailabilityTable
import ch.epfl.skysync.database.tables.BalloonTable
import ch.epfl.skysync.database.tables.BasketTable
import ch.epfl.skysync.database.tables.FlightMemberTable
import ch.epfl.skysync.database.tables.FlightTable
import ch.epfl.skysync.database.tables.FlightTypeTable
import ch.epfl.skysync.database.tables.MessageGroupTable
import ch.epfl.skysync.database.tables.MessageTable
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
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.FlightColor
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.message.Message
import ch.epfl.skysync.models.message.MessageGroup
import ch.epfl.skysync.models.user.Admin
import ch.epfl.skysync.models.user.Crew
import ch.epfl.skysync.models.user.Pilot
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Represent a mock database setup
 *
 * Define sample data and a standard database state ([fillDatabase]). Designed to be used on a blank
 * database, use [clearDatabase] to clear any existing data.
 */
class DatabaseSetup {
  var admin1 =
      Admin(
          id = "id-admin-1",
          firstname = "admin-1",
          lastname = "lastname",
          availabilities = AvailabilityCalendar(),
          assignedFlights = FlightGroupCalendar())
  var admin2 =
      Admin(
          id = "id-admin-2",
          firstname = "admin-2",
          lastname = "lastname",
          availabilities = AvailabilityCalendar(),
          assignedFlights = FlightGroupCalendar())
  var crew1 =
      Crew(
          id = "id-crew-1",
          firstname = "crew-1",
          lastname = "Bob",
          availabilities = AvailabilityCalendar(),
          assignedFlights = FlightGroupCalendar())
  var pilot1 =
      Pilot(
          id = "id-pilot-1",
          firstname = "pilot-1",
          lastname = "Bob",
          availabilities = AvailabilityCalendar(),
          assignedFlights = FlightGroupCalendar(),
          qualification = BalloonQualification.LARGE)
  var pilot2 =
      Pilot(
          id = "id-pilot-2",
          firstname = "pilot-2",
          lastname = "lastname",
          availabilities = AvailabilityCalendar(),
          assignedFlights = FlightGroupCalendar(),
          qualification = BalloonQualification.SMALL)
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
  var availability5 =
      Availability(
          status = AvailabilityStatus.MAYBE,
          timeSlot = TimeSlot.PM,
          date = LocalDate.of(2024, 8, 16))

  var balloon1 = Balloon(name = "balloon-1", qualification = BalloonQualification.MEDIUM)

  var balloon2 = Balloon(name = "balloon-2", qualification = BalloonQualification.LARGE)

  var basket1 = Basket(name = "basket-1", hasDoor = false)

  var basket2 = Basket(name = "basket-2", hasDoor = true)

  var flightType1 = FlightType.DISCOVERY
  var flightType2 = FlightType.FONDUE

  var vehicle1 = Vehicle(name = "vehicle-1")
  var vehicle2 = Vehicle(name = "vehicle-2")

  var flight1 =
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

  var flight4 =
      ConfirmedFlight(
          id = UNSET_ID,
          nPassengers = 2,
          team = Team(roles = listOf(Role(RoleType.PILOT, pilot1), Role(RoleType.CREW, crew1))),
          flightType = flightType1,
          balloon = balloon1,
          basket = basket1,
          date = LocalDate.of(2024, 8, 12),
          timeSlot = TimeSlot.PM,
          vehicles = listOf(vehicle2),
          remarks = listOf("remark 1", "remark 2"),
          color = FlightColor.BLUE,
          meetupTimeTeam = LocalTime.of(13, 30, 0),
          departureTimeTeam = LocalTime.of(13, 45, 0),
          meetupTimePassenger = LocalTime.of(14, 0, 0),
          meetupLocationPassenger = "location",
      )

  var messageGroup1 =
      MessageGroup(name = "Group 1", userIds = setOf(admin2.id, pilot1.id, crew1.id))
  var messageGroup2 = MessageGroup(name = "Group 2", userIds = setOf(admin1.id, admin2.id))

  var message1 =
      Message(user = admin2, date = Date.from(Instant.now().minusSeconds(20)), content = "Hello")
  var message2 =
      Message(user = pilot1, date = Date.from(Instant.now().minusSeconds(10)), content = "World")

  var message3 = Message(user = admin2, date = Date.from(Instant.now()), content = "Some stuff")

  /**
   * Delete all items in all tables of the database
   *
   * @param db Firestore database instance
   */
  suspend fun clearDatabase(db: FirestoreDatabase) = coroutineScope {
    listOf(
            launch { FlightTypeTable(db).deleteTable(onError = null) },
            launch { BalloonTable(db).deleteTable(onError = null) },
            launch { BasketTable(db).deleteTable(onError = null) },
            launch { VehicleTable(db).deleteTable(onError = null) },
            launch { FlightMemberTable(db).deleteTable(onError = null) },
            launch { UserTable(db).deleteTable(onError = null) },
            launch { FlightTable(db).deleteTable(onError = null) },
            launch { AvailabilityTable(db).deleteTable(onError = null) },
            launch { MessageTable(db).deleteTable(onError = null) },
            launch { MessageGroupTable(db).deleteTable(onError = null) },
        )
        .forEach { it.join() }
  }

  /**
   * Fill the database with a standard state
   *
   * @param db Firestore database instance
   */
  suspend fun fillDatabase(db: FirestoreDatabase) = coroutineScope {
    val flightTypeTable = FlightTypeTable(db)
    val balloonTable = BalloonTable(db)
    val basketTable = BasketTable(db)
    val vehicleTable = VehicleTable(db)
    val userTable = UserTable(db)
    val flightTable = FlightTable(db)
    val availabilityTable = AvailabilityTable(db)
    val messageTable = MessageTable(db)
    val messageGroupTable = MessageGroupTable(db)

    listOf(
            launch { flightType1 = flightType1.copy(id = flightTypeTable.add(flightType1)) },
            launch { flightType2 = flightType2.copy(id = flightTypeTable.add(flightType2)) },
            launch { balloon1 = balloon1.copy(id = balloonTable.add(balloon1)) },
            launch { balloon2 = balloon2.copy(id = balloonTable.add(balloon2)) },
            launch { basket1 = basket1.copy(id = basketTable.add(basket1)) },
            launch { basket2 = basket2.copy(id = basketTable.add(basket2)) },
            launch { vehicle1 = vehicle1.copy(id = vehicleTable.add(vehicle1)) },
            launch { vehicle2 = vehicle2.copy(id = vehicleTable.add(vehicle2)) },
            launch {
              messageGroup1 = messageGroup1.copy(id = messageGroupTable.add(messageGroup1))
            },
            launch {
              messageGroup2 = messageGroup2.copy(id = messageGroupTable.add(messageGroup2))
            },
            launch {
              userTable.set(admin1.id, admin1)
              availability3 =
                  availability3.copy(id = availabilityTable.add(admin1.id, availability3))
              availability4 =
                  availability4.copy(id = availabilityTable.add(admin1.id, availability4))
              admin1.availabilities.addCells(listOf(availability3, availability4))
            },
            launch { userTable.set(admin2.id, admin2) },
            launch {
              userTable.set(crew1.id, crew1)
              availability1 =
                  availability1.copy(id = availabilityTable.add(crew1.id, availability1))
              crew1.availabilities.addCells(listOf(availability1))
            },
            launch {
              userTable.set(pilot1.id, pilot1)
              availability2 =
                  availability2.copy(id = availabilityTable.add(pilot1.id, availability2))
              pilot1.availabilities.addCells(listOf(availability2))
            },
            launch {
              userTable.set(pilot2.id, pilot2)
              availability5 =
                  availability5.copy(id = availabilityTable.add(pilot2.id, availability5))
              pilot2.availabilities.addCells(listOf(availability5))
            },
        )
        .forEach { it.join() }

    // re-set all the objects that have been added in the db -> they now have IDs
    flight1 =
        flight1.copy(
            team = Team(roles = listOf(Role(RoleType.PILOT, pilot1), Role(RoleType.CREW, crew1))),
            flightType = flightType1,
            balloon = balloon1,
            basket = basket1,
            vehicles = listOf(vehicle1),
        )
    flight4 =
        flight4.copy(
            team = Team(roles = listOf(Role(RoleType.PILOT, pilot1), Role(RoleType.CREW, crew1))),
            flightType = flightType1,
            balloon = balloon1,
            basket = basket1,
            vehicles = listOf(vehicle2),
        )

    // now that the IDs are set, add the flights/messages
    listOf(
            launch { flight1 = flight1.copy(id = flightTable.add(flight1)) },
            launch { flight4 = flight4.copy(id = flightTable.add(flight4)) },
            launch { message1 = message1.copy(id = messageTable.add(messageGroup1.id, message1)) },
            launch { message2 = message2.copy(id = messageTable.add(messageGroup1.id, message2)) },
            launch { message3 = message3.copy(id = messageTable.add(messageGroup2.id, message3)) },
        )
        .forEach { it.join() }

    messageGroup1 = messageGroup1.copy(messages = listOf(message2, message1))
    messageGroup2 = messageGroup2.copy(messages = listOf(message3))
  }
}
