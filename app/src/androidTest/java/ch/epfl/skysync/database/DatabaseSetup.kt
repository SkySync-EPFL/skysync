package ch.epfl.skysync.database

import ch.epfl.skysync.database.DateUtility.createDate
import ch.epfl.skysync.database.tables.AvailabilityTable
import ch.epfl.skysync.database.tables.BalloonTable
import ch.epfl.skysync.database.tables.BasketTable
import ch.epfl.skysync.database.tables.FlightMemberTable
import ch.epfl.skysync.database.tables.FlightTable
import ch.epfl.skysync.database.tables.FlightTraceTable
import ch.epfl.skysync.database.tables.FlightTypeTable
import ch.epfl.skysync.database.tables.LocationTable
import ch.epfl.skysync.database.tables.MessageGroupTable
import ch.epfl.skysync.database.tables.MessageTable
import ch.epfl.skysync.database.tables.ReportTable
import ch.epfl.skysync.database.tables.TempUserTable
import ch.epfl.skysync.database.tables.UserTable
import ch.epfl.skysync.database.tables.VehicleTable
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.Availability
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.calendar.getTimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.flight.FlightColor
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.location.LocationPoint
import ch.epfl.skysync.models.message.Message
import ch.epfl.skysync.models.message.MessageGroup
import ch.epfl.skysync.models.reports.CrewReport
import ch.epfl.skysync.models.reports.PilotReport
import ch.epfl.skysync.models.user.Admin
import ch.epfl.skysync.models.user.Crew
import ch.epfl.skysync.models.user.Pilot
import ch.epfl.skysync.models.user.TempUser
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
 *
 * Users do not have availabilities nor assignedFlights filled, this is done by design as these
 * attributes have to be retrieve with specific methods (and not with a simple .get)
 */
class DatabaseSetup {
  var admin1 =
      Admin(
          id = "id-admin-1",
          firstname = "admin-1",
          lastname = "lastname-admin-1",
          email = "admin1.lastname@skysnc.ch",
      )
  var admin2 =
      Admin(
          id = "id-admin-2",
          firstname = "admin-2",
          lastname = "lastname-admin-2",
          email = "admin2.lastname@skysnc.ch",
      )
  var crew1 =
      Crew(
          id = "id-crew-1",
          firstname = "crew-1",
          lastname = "lastname-crew-1",
          email = "crew1.bob@skysnc.ch",
      )

  var crew2 =
      Crew(
          id = "id-crew-2",
          firstname = "crew-2",
          lastname = "lastname-crew-2",
          email = "crew2.denis@skysnc.ch",
      )

  var tempUser =
      TempUser(
          email = "temp.user@skysnc.ch",
          userRole = UserRole.CREW,
          firstname = "temp",
          lastname = "user",
          balloonQualification = null)
  var pilot1 =
      Pilot(
          id = "id-pilot-1",
          firstname = "pilot-1",
          lastname = "lastname-pilot-1",
          email = "pilot1.bob@skysnc.ch",
          qualification = BalloonQualification.LARGE)
  var pilot2 =
      Pilot(
          id = "id-pilot-2",
          firstname = "pilot-2",
          lastname = "lastname-pilot-2",
          email = "pilot2.lastname@skysnc.ch",
          qualification = BalloonQualification.SMALL)

  var pilot3 =
      Pilot(
          id = "id-pilot-3",
          firstname = "pilot-3",
          lastname = "pilot",
          email = "pilot3.pilot@skysnc.ch",
          qualification = BalloonQualification.SMALL)

  var allUsers = listOf(crew1, crew2, pilot1, pilot2, pilot3, admin1, admin2)
  val allCrews = allUsers.filterIsInstance<Crew>()
  val allPilots = allUsers.filterIsInstance<Pilot>()
  val allAdmins = allUsers.filterIsInstance<Admin>()

  var date1 = LocalDate.of(2024, 8, 14)

  // this the date of flight4, it needs to be today for the InFlightViewModel tests
  var date2 = LocalDate.now()
  var date2TimeSlot = getTimeSlot(LocalTime.now())
  var date2TimeSlotInverse = if (date2TimeSlot == TimeSlot.AM) TimeSlot.PM else TimeSlot.AM
  var dateNoFlight = LocalDate.of(2024, 8, 16)

  var availability1Crew1 =
      Availability(status = AvailabilityStatus.OK, timeSlot = TimeSlot.AM, date = date1)
  var availability2Crew1 =
      Availability(status = AvailabilityStatus.OK, timeSlot = TimeSlot.PM, date = date1)
  var availability3Crew1 =
      Availability(status = AvailabilityStatus.OK, timeSlot = date2TimeSlot, date = date2)
  var availability4Crew1 =
      Availability(status = AvailabilityStatus.OK, timeSlot = date2TimeSlotInverse, date = date2)

  var availability1Crew2 =
      Availability(status = AvailabilityStatus.OK, timeSlot = TimeSlot.AM, date = date1)
  var availability2Crew2 =
      Availability(status = AvailabilityStatus.OK, timeSlot = TimeSlot.PM, date = date1)
  var availability3Crew2 =
      Availability(status = AvailabilityStatus.OK, timeSlot = date2TimeSlot, date = date2)
  var availability4Crew2 =
      Availability(status = AvailabilityStatus.OK, timeSlot = date2TimeSlotInverse, date = date2)

  var availability1Pilot1 =
      Availability(status = AvailabilityStatus.OK, timeSlot = TimeSlot.AM, date = date1)
  var availability2Pilot1 =
      Availability(status = AvailabilityStatus.OK, timeSlot = TimeSlot.PM, date = date1)
  var availability3Pilot1 =
      Availability(status = AvailabilityStatus.OK, timeSlot = date2TimeSlot, date = date2)
  var availability4Pilot1 =
      Availability(status = AvailabilityStatus.OK, timeSlot = date2TimeSlotInverse, date = date2)

  var availability1Pilot2 =
      Availability(status = AvailabilityStatus.OK, timeSlot = TimeSlot.AM, date = date1)
  var availability2Pilot2 =
      Availability(status = AvailabilityStatus.NO, timeSlot = TimeSlot.PM, date = date1)
  var availability3Pilot2 =
      Availability(status = AvailabilityStatus.OK, timeSlot = TimeSlot.AM, date = dateNoFlight)

  var availability1Admin1 =
      Availability(status = AvailabilityStatus.NO, timeSlot = TimeSlot.AM, date = date1)
  var availability2Admin1 =
      Availability(status = AvailabilityStatus.OK, timeSlot = date2TimeSlotInverse, date = date2)

  var balloon1 = Balloon(name = "balloon-1", qualification = BalloonQualification.MEDIUM)

  var balloon2 = Balloon(name = "balloon-2", qualification = BalloonQualification.LARGE)

  var balloon3 = Balloon(name = "balloon-3", qualification = BalloonQualification.SMALL)

  var basket1 = Basket(name = "basket-1", hasDoor = false)

  var basket2 = Basket(name = "basket-2", hasDoor = true)

  var basket3 = Basket(name = "basket-3", hasDoor = true)

  var flightType1 = FlightType.DISCOVERY
  var flightType2 = FlightType.FONDUE

  var vehicle1 = Vehicle(name = "vehicle-1")
  var vehicle2 = Vehicle(name = "vehicle-2")
  var vehicle3 = Vehicle(name = "vehicle-3")

  var flight1 =
      PlannedFlight(
          nPassengers = 2,
          team = Team(roles = listOf(Role(RoleType.PILOT, pilot1), Role(RoleType.CREW, crew1))),
          flightType = flightType1,
          balloon = balloon1,
          basket = basket1,
          date = date1,
          timeSlot = TimeSlot.AM,
          vehicles = listOf(vehicle1),
          id = UNSET_ID)

  var flight2 =
      PlannedFlight(
          nPassengers = 4,
          team = Team(roles = listOf(Role(RoleType.PILOT, pilot2), Role(RoleType.CREW, crew2))),
          flightType = flightType2,
          balloon = balloon2,
          basket = basket2,
          date = date1,
          timeSlot = TimeSlot.AM,
          vehicles = listOf(vehicle2, vehicle3),
          id = UNSET_ID)

  var flight3 =
      PlannedFlight(
          nPassengers = 3,
          team = Team(roles = listOf(Role(RoleType.PILOT, pilot1), Role(RoleType.CREW, crew1))),
          flightType = flightType1,
          balloon = balloon3,
          basket = basket3,
          date = date1,
          timeSlot = TimeSlot.PM,
          vehicles = listOf(vehicle1, vehicle2),
          id = UNSET_ID)

  var flight4 =
      ConfirmedFlight(
          id = UNSET_ID,
          nPassengers = 2,
          team =
              Team(
                  roles =
                      listOf(
                          Role(RoleType.PILOT, pilot1),
                          Role(RoleType.CREW, crew1),
                          Role(RoleType.CREW, crew2))),
          flightType = flightType1,
          balloon = balloon1,
          basket = basket1,
          date = date2,
          timeSlot = date2TimeSlot,
          vehicles = listOf(vehicle2),
          remarks = listOf("remark 1", "remark 2"),
          color = FlightColor.BLUE,
          meetupTimeTeam = LocalTime.of(13, 30, 0),
          departureTimeTeam = LocalTime.of(13, 45, 0),
          meetupTimePassenger = LocalTime.of(14, 0, 0),
          meetupLocationPassenger = "location",
      )
  val landingTime1 = createDate(date1.year, date1.monthValue, date1.dayOfMonth, 5, 0)
  val takeOffTime1 = createDate(date1.year, date1.monthValue, date1.dayOfMonth, 4, 0)
  val landingTime2 = createDate(date1.year, date1.monthValue, date1.dayOfMonth, 7, 0)
  val takeOffTime2 = createDate(date1.year, date1.monthValue, date1.dayOfMonth, 6, 0)

  val landingLocation1 = LocationPoint(0, 0.0, 0.0, "Landing")
  val takeOffLocation1 = LocationPoint(0, 1.0, 1.0, "Take off")
  var finishedFlight1 =
      FinishedFlight(
          id = UNSET_ID,
          nPassengers = 2,
          team = Team(roles = listOf(Role(RoleType.PILOT, pilot1), Role(RoleType.CREW, crew1))),
          flightType = flightType1,
          balloon = balloon1,
          basket = basket1,
          date = date1,
          timeSlot = TimeSlot.AM,
          vehicles = listOf(vehicle2),
          color = FlightColor.GREEN,
          flightTime = takeOffTime1.time - landingTime1.time,
          landingTime = landingTime1,
          takeOffTime = takeOffTime1,
          landingLocation = landingLocation1,
          takeOffLocation = takeOffLocation1,
      )

  var finishedFlight2 =
      FinishedFlight(
          id = UNSET_ID,
          nPassengers = 4,
          team = Team(roles = listOf(Role(RoleType.PILOT, pilot1), Role(RoleType.CREW, crew2))),
          flightType = flightType2,
          balloon = balloon2,
          basket = basket2,
          date = date1,
          timeSlot = TimeSlot.AM,
          vehicles = listOf(vehicle2),
          color = FlightColor.GREEN,
          flightTime = takeOffTime2.time - landingTime2.time,
          landingTime = landingTime2,
          takeOffTime = takeOffTime2,
          landingLocation = landingLocation1,
          takeOffLocation = takeOffLocation1,
      )

  var flight5 =
      FinishedFlight(
          "1234",
          3,
          Team(listOf(Role(RoleType.CREW), Role(RoleType.CREW))),
          FlightType.DISCOVERY,
          Balloon("Balloon Name", BalloonQualification.LARGE, "Ballon Name"),
          Basket("Basket Name", true, "1234"),
          LocalDate.now().plusDays(3),
          TimeSlot.PM,
          listOf(
              Vehicle("Peugeot 308", "1234"),
              Vehicle("Peugeot 308", "1234"),
              Vehicle("Peugeot 308", "1234"),
          ),
          color = FlightColor.RED,
          takeOffTime = Date.from(Instant.now().minusSeconds(12)),
          takeOffLocation = LocationPoint(22, 46.217033, 6.084294, "Vernier, 1214"),
          landingTime = Date.from(Instant.now()),
          landingLocation = LocationPoint(21, 46.217030, 6.084290, "Vernier"),
          flightTime = 2000000)

  var messageGroup1 =
      MessageGroup(
          name = "Group 1",
          color = FlightColor.RED,
          userIds = setOf(admin2.id, pilot1.id, crew1.id))
  var messageGroup2 =
      MessageGroup(
          name = "Group 2", color = FlightColor.GREEN, userIds = setOf(admin1.id, admin2.id))

  var message1 =
      Message(user = admin2, date = Date.from(Instant.now().minusSeconds(20)), content = "Hello")
  var message2 =
      Message(user = pilot1, date = Date.from(Instant.now().minusSeconds(10)), content = "World")

  var message3 = Message(user = admin2, date = Date.from(Instant.now()), content = "Some stuff")

  var report1 =
      PilotReport(
          id = finishedFlight1.id,
          author = pilot1.id,
          effectivePax = 2,
          takeOffTime = takeOffTime2,
          landingTime = landingTime2,
          takeOffLocation = takeOffLocation1,
          landingLocation = landingLocation1,
          begin = takeOffTime2,
          end = landingTime2,
          pauseDuration = 0,
          comments = "Some comments 1",
      )
  var report2 =
      CrewReport(
          id = finishedFlight1.id,
          author = crew1.id,
          begin = takeOffTime2,
          end = landingTime2,
          pauseDuration = 0,
          comments = "Some comments 2",
      )

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
            launch { TempUserTable(db).deleteTable(onError = null) },
            launch { FlightTable(db).deleteTable(onError = null) },
            launch { FlightTraceTable(db).deleteTable(onError = null) },
            launch { AvailabilityTable(db).deleteTable(onError = null) },
            launch { MessageTable(db).deleteTable(onError = null) },
            launch { MessageGroupTable(db).deleteTable(onError = null) },
            launch { LocationTable(db).deleteTable(onError = null) },
            launch { ReportTable(db).deleteTable(onError = null) },
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
    val tempUserTable = TempUserTable(db)
    val flightTable = FlightTable(db)
    val availabilityTable = AvailabilityTable(db)
    val messageTable = MessageTable(db)
    val messageGroupTable = MessageGroupTable(db)
    val reportTable = ReportTable(db)

    listOf(
            launch { flightType1 = flightType1.copy(id = flightTypeTable.add(flightType1)) },
            launch { flightType2 = flightType2.copy(id = flightTypeTable.add(flightType2)) },
            launch { balloon1 = balloon1.copy(id = balloonTable.add(balloon1)) },
            launch { balloon2 = balloon2.copy(id = balloonTable.add(balloon2)) },
            launch { balloon3 = balloon3.copy(id = balloonTable.add(balloon3)) },
            launch { basket1 = basket1.copy(id = basketTable.add(basket1)) },
            launch { basket2 = basket2.copy(id = basketTable.add(basket2)) },
            launch { basket3 = basket3.copy(id = basketTable.add(basket3)) },
            launch { vehicle1 = vehicle1.copy(id = vehicleTable.add(vehicle1)) },
            launch { vehicle2 = vehicle2.copy(id = vehicleTable.add(vehicle2)) },
            launch { vehicle3 = vehicle3.copy(id = vehicleTable.add(vehicle3)) },
            launch {
              messageGroup1 = messageGroup1.copy(id = messageGroupTable.add(messageGroup1))
            },
            launch {
              messageGroup2 = messageGroup2.copy(id = messageGroupTable.add(messageGroup2))
            },
            launch {
              userTable.set(admin1.id, admin1)
              availability1Admin1 =
                  availability1Admin1.copy(
                      id = availabilityTable.add(admin1.id, availability1Admin1))
              availability2Admin1 =
                  availability2Admin1.copy(
                      id = availabilityTable.add(admin1.id, availability2Admin1))
            },
            launch { userTable.set(admin2.id, admin2) },
            launch {
              userTable.set(crew1.id, crew1)
              availability1Crew1 =
                  availability1Crew1.copy(id = availabilityTable.add(crew1.id, availability1Crew1))
              availability2Crew1 =
                  availability2Crew1.copy(id = availabilityTable.add(crew1.id, availability2Crew1))
              availability3Crew1 =
                  availability3Crew1.copy(id = availabilityTable.add(crew1.id, availability3Crew1))
              availability4Crew1 =
                  availability4Crew1.copy(id = availabilityTable.add(crew1.id, availability4Crew1))
            },
            launch {
              userTable.set(pilot1.id, pilot1)
              availability1Pilot1 =
                  availability1Pilot1.copy(
                      id = availabilityTable.add(pilot1.id, availability1Pilot1))
              availability2Pilot1 =
                  availability2Pilot1.copy(
                      id = availabilityTable.add(pilot1.id, availability2Pilot1))
              availability3Pilot1 =
                  availability3Pilot1.copy(
                      id = availabilityTable.add(pilot1.id, availability3Pilot1))
              availability4Pilot1 =
                  availability4Pilot1.copy(
                      id = availabilityTable.add(pilot1.id, availability4Pilot1))
            },
            launch {
              userTable.set(crew2.id, crew2)
              availability1Crew2 =
                  availability1Crew2.copy(id = availabilityTable.add(crew2.id, availability1Crew2))
              availability2Crew2 =
                  availability2Crew2.copy(id = availabilityTable.add(crew2.id, availability2Crew2))
              availability3Crew2 =
                  availability3Crew2.copy(id = availabilityTable.add(crew2.id, availability3Crew2))
              availability4Crew2 =
                  availability4Crew2.copy(id = availabilityTable.add(crew2.id, availability4Crew2))
            },
            launch {
              userTable.set(pilot2.id, pilot2)
              availability1Pilot2 =
                  availability1Pilot2.copy(
                      id = availabilityTable.add(pilot2.id, availability1Pilot2))

              availability2Pilot2 =
                  availability2Pilot2.copy(
                      id = availabilityTable.add(pilot2.id, availability2Pilot2))
              availability3Pilot2 =
                  availability3Pilot2.copy(
                      id = availabilityTable.add(pilot2.id, availability3Pilot2))
            },
            launch { userTable.set(pilot3.id, pilot3) },
            launch { tempUserTable.set(tempUser.email, tempUser) })
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
    flight2 =
        flight2.copy(
            team = Team(roles = listOf(Role(RoleType.PILOT, pilot2), Role(RoleType.CREW, crew2))),
            flightType = flightType1,
            balloon = balloon2,
            basket = basket2,
            vehicles = listOf(vehicle2, vehicle3),
        )
    flight3 =
        flight3.copy(
            team = Team(roles = listOf(Role(RoleType.PILOT, pilot1), Role(RoleType.CREW, crew1))),
            flightType = flightType1,
            balloon = balloon3,
            basket = basket3,
            vehicles = listOf(vehicle1, vehicle2),
        )
    flight4 =
        flight4.copy(
            team =
                Team(
                    roles =
                        listOf(
                            Role(RoleType.PILOT, pilot1),
                            Role(RoleType.CREW, crew1),
                            Role(RoleType.CREW, crew2))),
            flightType = flightType1,
            balloon = balloon1,
            basket = basket1,
            vehicles = listOf(vehicle2),
        )
    finishedFlight1 =
        finishedFlight1.copy(
            team = Team(roles = listOf(Role(RoleType.PILOT, pilot1), Role(RoleType.CREW, crew1))),
            flightType = flightType1,
            balloon = balloon1,
            basket = basket1,
            vehicles = listOf(vehicle2),
        )
    finishedFlight2 =
        finishedFlight2.copy(
            team = Team(roles = listOf(Role(RoleType.PILOT, pilot1), Role(RoleType.CREW, crew2))),
            flightType = flightType1,
            balloon = balloon1,
            basket = basket1,
            vehicles = listOf(vehicle2),
        )

    // now that the IDs are set, add the flights/messages
    listOf(
            launch { flight1 = flight1.copy(id = flightTable.add(flight1)) },
            launch { flight2 = flight2.copy(id = flightTable.add(flight2)) },
            launch { flight3 = flight3.copy(id = flightTable.add(flight3)) },
            launch { flight4 = flight4.copy(id = flightTable.add(flight4)) },
            launch {
              finishedFlight1 = finishedFlight1.copy(id = flightTable.add(finishedFlight1))
            },
            launch {
              finishedFlight2 = finishedFlight2.copy(id = flightTable.add(finishedFlight2))
            },
            launch { message1 = message1.copy(id = messageTable.add(messageGroup1.id, message1)) },
            launch { message2 = message2.copy(id = messageTable.add(messageGroup1.id, message2)) },
            launch { message3 = message3.copy(id = messageTable.add(messageGroup2.id, message3)) },
        )
        .forEach { it.join() }

    listOf(
            launch { report1 = report1.copy(id = reportTable.add(report1, finishedFlight1.id)) },
            launch { report2 = report2.copy(id = reportTable.add(report2, finishedFlight1.id)) },
        )
        .forEach { it.join() }

    messageGroup1 = messageGroup1.copy(messages = listOf(message2, message1))
    messageGroup2 = messageGroup2.copy(messages = listOf(message3))
  }
}
