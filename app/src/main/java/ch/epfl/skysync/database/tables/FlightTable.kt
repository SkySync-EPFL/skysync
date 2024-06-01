package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.DateUtility
import ch.epfl.skysync.database.DbFlightStatus
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.ListenerUpdate
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.FlightMemberSchema
import ch.epfl.skysync.database.schemas.FlightSchema
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.location.FlightTrace
import ch.epfl.skysync.models.location.LocationPoint
import ch.epfl.skysync.models.reports.Report
import ch.epfl.skysync.models.user.User
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Represents the "flight" table in the database.
 *
 * @property db The FirestoreDatabase instance for interacting with the Firestore database.
 */
class FlightTable(db: FirestoreDatabase) :
    Table<Flight, FlightSchema>(db, FlightSchema::class, PATH) {
  private val flightTypeTable = FlightTypeTable(db)
  private val balloonTable = BalloonTable(db)
  private val basketTable = BasketTable(db)
  private val vehicleTable = VehicleTable(db)
  private val flightMemberTable = FlightMemberTable(db)
  private val userTable = UserTable(db)
  private val reportTable = ReportTable(db)

  /**
   * Creates a [Flight] instance from the flight schema and the retrieved entities.
   *
   * @param schema The FlightSchema instance.
   * @param flightType The FlightType instance.
   * @param balloon The Balloon instance.
   * @param basket The Basket instance.
   * @param vehicles The list of Vehicle instances.
   * @param team The Team instance.
   * @param flightTrace The FlightTrace instance.
   * @param reports The list of Report instances.
   * @return The created Flight instance.
   */
  private fun makeFlight(
      schema: FlightSchema,
      flightType: FlightType,
      balloon: Balloon?,
      basket: Basket?,
      vehicles: List<Vehicle>,
      team: Team,
      flightTrace: FlightTrace?,
      reports: List<Report>? = emptyList(),
  ): Flight {
    return when (schema.status!!) {
      DbFlightStatus.PLANNED ->
          PlannedFlight(
              id = schema.id!!,
              nPassengers = schema.nPassengers!!,
              team = team,
              flightType = flightType,
              balloon = balloon,
              basket = basket,
              date = DateUtility.dateToLocalDate(schema.date!!),
              timeSlot = schema.timeSlot!!,
              vehicles = vehicles)
      DbFlightStatus.CONFIRMED -> {
        // balloon and basket must be specified in confirmed flight
        if (balloon == null || basket == null) {
          throw Exception("Internal error: Invalid confirmed flight.")
        }
        ConfirmedFlight(
            id = schema.id!!,
            nPassengers = schema.nPassengers!!,
            team = team,
            flightType = flightType,
            balloon = balloon,
            basket = basket,
            date = DateUtility.dateToLocalDate(schema.date!!),
            timeSlot = schema.timeSlot!!,
            vehicles = vehicles,
            remarks = schema.remarks!!,
            color = schema.color!!,
            meetupTimeTeam = DateUtility.stringToLocalTime(schema.meetupTimeTeam!!),
            departureTimeTeam = DateUtility.stringToLocalTime(schema.departureTimeTeam!!),
            meetupTimePassenger = DateUtility.stringToLocalTime(schema.meetupTimePassenger!!),
            meetupLocationPassenger = schema.meetupLocationPassenger!!,
            isOngoing = schema.isOngoing!!,
            startTimestamp = schema.startTimestamp,
        )
      }
      DbFlightStatus.FINISHED ->
          FinishedFlight(
              id = schema.id!!,
              nPassengers = schema.nPassengers!!,
              team = team,
              flightType = flightType,
              balloon = balloon!!,
              basket = basket!!,
              date = DateUtility.dateToLocalDate(schema.date!!),
              timeSlot = schema.timeSlot!!,
              vehicles = vehicles,
              color = schema.color!!,
              takeOffTime =
                  DateUtility.hourMinuteStringToDate(
                      schema.takeOffTime!!, DateUtility.dateToLocalDate(schema.date)),
              takeOffLocation =
                  LocationPoint(
                      0, schema.takeOffLocationLat!!, schema.takeOffLocationLong!!, "TakeOffSpot"),
              landingTime =
                  DateUtility.hourMinuteStringToDate(
                      schema.landingTime!!, DateUtility.dateToLocalDate(schema.date)),
              landingLocation =
                  LocationPoint(
                      0, schema.landingLocationLat!!, schema.landingLocationLong!!, "LandingSpot"),
              flightTime = schema.flightTime!!,
              reportId = reports!!,
              flightTrace = flightTrace!!)
    }
  }

  /**
   * Adds a listener on a flight.
   *
   * The listener will be triggered each time the flight is updated.
   *
   * @param flightId The ID of the flight.
   * @param onChange Callback called each time the listener is triggered, passed the adds, updates,
   *   deletes that happened since the last listener trigger.
   * @param coroutineScope The CoroutineScope instance.
   * @param onError Callback called when an error occurs.
   * @return The ListenerRegistration instance.
   */
  fun addFlightListener(
      flightId: String,
      onChange: suspend (ListenerUpdate<Flight>) -> Unit,
      coroutineScope: CoroutineScope,
      onError: ((Exception) -> Unit)? = null
  ): ListenerRegistration {
    return queryListener(
        Filter.equalTo(FieldPath.documentId(), flightId),
        onChange = { update ->
          coroutineScope {
            withErrorCallback(onError) {
              val adds = update.adds.map { async { retrieveFlight(it) } }
              val updates = update.updates.map { async { retrieveFlight(it) } }
              val deletes = update.deletes.map { async { retrieveFlight(it) } }
              onChange(
                  ListenerUpdate(
                      isFirstUpdate = update.isFirstUpdate,
                      isLocalUpdate = update.isLocalUpdate,
                      adds = adds.awaitAll().filterNotNull(),
                      updates = updates.awaitAll().filterNotNull(),
                      deletes = deletes.awaitAll().filterNotNull(),
                  ))
            }
          }
        },
        coroutineScope = coroutineScope)
  }

  /**
   * Adds items to the flight-member relation for each role defined in the team, whether or not it
   * has a user assigned.
   *
   * @param flightId The ID of the flight.
   * @param team The Team instance.
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
   * Sets items to the flight-member relation for each role defined in the team, whether or not it
   * has a user assigned.
   *
   * @param flightId The ID of the flight.
   * @param team The Team instance.
   */
  private suspend fun setTeam(
      flightId: String,
      team: Team,
  ) {
    flightMemberTable.queryDelete(Filter.equalTo("flightId", flightId))
    addTeam(flightId, team)
  }

  /**
   * Retrieves the team members of the flight.
   *
   * First query the flight-member relation then for the roles which have a user assigned, query the
   * user table.
   *
   * @param schema The FlightSchema instance.
   * @return The Team instance.
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

  /**
   * Retrieves the vehicles linked to the flight.
   *
   * @param schema The FlightSchema instance.
   * @return The list of Vehicle instances.
   */
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

  /**
   * Retrieves all the entities linked to the flight.
   *
   * @param flightSchema The FlightSchema instance.
   * @return The Flight instance.
   */
  private suspend fun retrieveFlight(flightSchema: FlightSchema): Flight? = coroutineScope {
    var flightType: FlightType? = null
    var balloon: Balloon? = null
    var basket: Basket? = null
    var vehicles: List<Vehicle>? = null
    var team: Team? = null
    var reports: List<Report>? = null
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
            launch {
              if (flightSchema.id == null) return@launch
              reports = reportTable.retrieveReports(flightSchema.id)
            },
            launch { vehicles = retrieveVehicles(flightSchema) },
            launch { team = retrieveTeam(flightSchema) })
    jobs.forEach { it.join() }
    makeFlight(
        schema = flightSchema,
        flightType = flightType!!,
        balloon = balloon,
        basket = basket,
        vehicles = vehicles!!,
        team = team!!,
        reports = reports,
        flightTrace = FlightTrace(flightSchema.id!!, emptyList()))
  }

  /**
   * Retrieves a flight by its ID.
   *
   * @param id The ID of the flight.
   * @param onError Callback called when an error occurs.
   * @return The Flight instance.
   */
  override suspend fun get(id: String, onError: ((Exception) -> Unit)?): Flight? {
    return withErrorCallback(onError) {
      val schema = db.getItem(path, id, clazz) ?: return@withErrorCallback null
      retrieveFlight(schema)
    }
  }

  /**
   * Retrieves all flights.
   *
   * @param onError Callback called when an error occurs.
   * @return The list of Flight instances.
   */
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

  /**
   * Queries flights based on a filter.
   *
   * @param filter The Filter instance.
   * @param limit The maximum number of results to return.
   * @param orderBy The field to order by.
   * @param orderByDirection The direction to order by.
   * @param onError Callback called when an error occurs.
   * @return The list of Flight instances.
   */
  override suspend fun query(
      filter: Filter,
      limit: Long?,
      orderBy: String?,
      orderByDirection: Query.Direction,
      onError: ((Exception) -> Unit)?
  ): List<Flight> = coroutineScope {
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
   * Adds a new flight to the database.
   *
   * This will generate a new id for this flight and disregard any previously set id. This will
   * create the [Flight.team] (and in the process the [User.assignedFlights])
   *
   * @param item The flight to add to the database.
   * @param onError Callback called when an error occurs.
   * @return The ID of the added flight.
   */
  suspend fun add(item: Flight, onError: ((Exception) -> Unit)? = null): String {
    return withErrorCallback(onError) {
      val flightId = db.addItem(path, FlightSchema.fromModel(item))
      addTeam(flightId, item.team)
      if (item is FinishedFlight && item.reportId.isNotEmpty()) {
        reportTable.addAll(item.reportId, flightId)
      }
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
                  launch { setTeam(id, item.team) },
                  launch {
                    if (item is FinishedFlight && item.reportId.isNotEmpty()) {
                      reportTable.addAll(item.reportId, id)
                    }
                  })
              .forEach { it.join() }
        }
      }

  /**
   * Deletes a flight by its ID.
   *
   * @param id The ID of the flight.
   * @param onError Callback called when an error occurs.
   */
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
