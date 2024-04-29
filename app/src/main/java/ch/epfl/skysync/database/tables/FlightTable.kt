package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.DateLocalDateConverter
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.FlightStatus
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.FlightMemberSchema
import ch.epfl.skysync.database.schemas.FlightSchema
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.user.User
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Filter
import java.time.LocalDate
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/** Represent the "flight" table */
class FlightTable(db: FirestoreDatabase) :
    Table<Flight, FlightSchema>(db, FlightSchema::class, PATH) {
  private val flightTypeTable = FlightTypeTable(db)
  private val balloonTable = BalloonTable(db)
  private val basketTable = BasketTable(db)
  private val vehicleTable = VehicleTable(db)
  private val flightMemberTable = FlightMemberTable(db)
  private val userTable = UserTable(db)

  /** Create a [Flight] instance from the flight schema and the retrieved entities */
  private fun makeFlight(
      schema: FlightSchema,
      flightType: FlightType,
      balloon: Balloon?,
      basket: Basket?,
      vehicles: List<Vehicle>,
      team: Team
  ): Flight {
    return when (schema.status!!) {
      FlightStatus.PLANNED ->
          PlannedFlight(
              id = schema.id!!,
              nPassengers = schema.numPassengers!!,
              team = team,
              flightType = flightType,
              balloon = balloon,
              basket = basket,
              date = DateLocalDateConverter.dateToLocalDate(schema.date!!),
              timeSlot = schema.timeSlot!!,
              vehicles = vehicles)
      FlightStatus.CONFIRMED -> throw NotImplementedError()
      FlightStatus.FINISHED -> throw NotImplementedError()
    }
  }

  /**
   * Add items to the flight-member relation for each role defined in the team, whether or not it
   * has a user assigned.
   */
  private suspend fun addTeam(
      flightId: String,
      team: Team,
  ): Unit = coroutineScope {
    team.roles
        .map { role ->
          async {
            flightMemberTable.add(
                FlightMemberSchema(
                    userId = role.assignedUser?.id, flightId = flightId, roleType = role.roleType))
          }
        }
        .awaitAll()
  }

  /**
   * Set items to the flight-member relation for each role defined in the team, whether or not it
   * has a user assigned.
   */
  private suspend fun setTeam(
      flightId: String,
      team: Team,
  ) {
    flightMemberTable.queryDelete(Filter.equalTo("flightId", flightId))
    addTeam(flightId, team)
  }

  /**
   * Retrieve the team members of the flight
   *
   * First query the flight-member relation then for the roles which have a user assigned, query the
   * user table.
   */
  private suspend fun retrieveTeam(schema: FlightSchema): Team = coroutineScope {
    val members = flightMemberTable.query(Filter.equalTo("flightId", schema.id))
    val roles = mutableListOf<Role>()
    val deferreds = mutableListOf<Job>()
    for (member in members) {
      if (member.userId == null) {
        roles.add(Role(member.roleType!!, null))
        continue
      }
      deferreds.add(
          launch {
            val user = userTable.get(member.userId)
            if (user == null) {
              // report
            } else {
              roles.add(Role(member.roleType!!, user))
            }
          })
    }
    deferreds.forEach { it.join() }
    Team(roles)
  }

  /** Retrieve the vehicles linked to the flight */
  private suspend fun retrieveVehicles(schema: FlightSchema): List<Vehicle> = coroutineScope {
    schema.vehicleIds!!
        .map { vehicleId ->
          async {
            val vehicle = vehicleTable.get(vehicleId)
            if (vehicle == null) {
              // report
            }
            vehicle
          }
        }
        .awaitAll()
        .filterNotNull()
  }

  /** Retrieve all the entities linked to the flight */
  private suspend fun retrieveFlight(flightSchema: FlightSchema): Flight? = coroutineScope {
    var flightType: FlightType? = null
    var balloon: Balloon? = null
    var basket: Basket? = null
    var vehicles: List<Vehicle>? = null
    var team: Team? = null
    val jobs =
        listOf(
            launch {
              flightType = flightTypeTable.get(flightSchema.flightTypeId!!)
              if (flightType == null) {
                // report
              }
            },
            launch {
              if (flightSchema.balloonId == null) return@launch
              balloon = balloonTable.get(flightSchema.balloonId!!)
              if (balloon == null) {
                // report
              }
            },
            launch {
              if (flightSchema.basketId == null) return@launch
              basket = basketTable.get(flightSchema.basketId!!)
              if (basket == null) {
                // report
              }
            },
            launch { vehicles = retrieveVehicles(flightSchema) },
            launch { team = retrieveTeam(flightSchema) })
    jobs.forEach { it.join() }
    makeFlight(flightSchema, flightType!!, balloon, basket, vehicles!!, team!!)
  }

  override suspend fun get(id: String, onError: ((Exception) -> Unit)?): Flight? {
    return withErrorCallback(onError) {
      val schema = db.getItem(path, id, clazz) ?: return@withErrorCallback null
      retrieveFlight(schema)
    }
  }

  override suspend fun getAll(onError: ((Exception) -> Unit)?): List<Flight> = coroutineScope {
    withErrorCallback(onError) {
      val schemas = db.getAll(path, clazz)
      schemas
          .map { schema ->
            async {
              val flight = retrieveFlight(schema)
              if (flight == null) {
                // report
              }
              flight!!
            }
          }
          .awaitAll()
    }
  }

  override suspend fun query(filter: Filter, onError: ((Exception) -> Unit)?): List<Flight> =
      coroutineScope {
        withErrorCallback(onError) {
          val schemas = db.query(path, filter, clazz)
          schemas
              .map { schema ->
                async {
                  val flight = retrieveFlight(schema)
                  if (flight == null) {
                    // report
                  }
                  flight!!
                }
              }
              .awaitAll()
        }
      }

  /**
   * Returns the available baskets on a given date and timeslot
   *
   * @param localDate The requested day
   * @param timeslot The requested timeslot
   */
  suspend fun getBasketsAvailableOn(localDate: LocalDate, timeslot: TimeSlot): List<Basket> {
    val dateFilter = Filter.equalTo("date", DateLocalDateConverter.localDateToDate(localDate))
    val timeslotFilter = Filter.equalTo("timeSlot", timeslot)
    val flightFilter = Filter.and(dateFilter, timeslotFilter)

    val unavailableBasketsIds: List<String> =
        query(flightFilter).mapNotNull { flight: Flight -> flight.basket?.id }

    val basketFilter = Filter.notInArray(FieldPath.documentId(), unavailableBasketsIds)
    return basketTable.query(basketFilter)
  }

  /**
   * Returns the all the flights of a given user
   *
   * @param id The id of the user
   */
  suspend fun getFlightsForUser(id: String): List<Flight> {
    val flightIds =
        flightMemberTable.query(Filter.equalTo("userId", id)).map { flightMemberSchema ->
          flightMemberSchema.flightId
        }
    return query(Filter.inArray(FieldPath.documentId(), flightIds))
  }

  /**
   * >>>>>>> Stashed changes Add a new flight to the database
   *
   * This will generate a new id for this flight and disregard any previously set id. This will
   * create the [Flight.team] (and in the process the [User.assignedFlights])
   *
   * @param item The flight to add to the database
   * @param onError Callback called when an error occurs
   */
  suspend fun add(item: Flight, onError: ((Exception) -> Unit)? = null): String {
    return withErrorCallback(onError) {
      val flightId = db.addItem(path, FlightSchema.fromModel(item))
      addTeam(flightId, item.team)
      flightId
    }
  }

  /**
   * Update a flight
   *
   * This will overwrite the flight at the given id.
   *
   * @param id The id of the flight
   * @param item The flight to update in the database
   * @param onError Callback called when an error occurs
   */
  suspend fun update(id: String, item: Flight, onError: ((Exception) -> Unit)? = null) =
      coroutineScope {
        withErrorCallback(onError) {
          listOf(
                  launch { db.setItem(path, id, FlightSchema.fromModel(item)) },
                  launch { setTeam(id, item.team) })
              .forEach { it.join() }
        }
      }

  override suspend fun delete(id: String, onError: ((Exception) -> Unit)?) = coroutineScope {
    withErrorCallback(onError) {
      listOf(
              launch { super.delete(id, onError = null) },
              launch { flightMemberTable.queryDelete(Filter.equalTo("flightId", id)) })
          .forEach { it.join() }
    }
  }

  companion object {
    const val PATH = "flight"
  }
}
