package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.DateLocalDateConverter
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.FlightStatus
import ch.epfl.skysync.database.ParallelOperationsEndCallback
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.FlightMemberSchema
import ch.epfl.skysync.database.schemas.FlightSchema
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.user.User
import com.google.firebase.firestore.Filter

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
  private fun addTeam(
      flightId: String,
      team: Team,
      onCompletion: () -> Unit,
      onError: (Exception) -> Unit
  ) {
    val delayedCallback = ParallelOperationsEndCallback(team.roles.size) { onCompletion() }
    for (role in team.roles) {
      flightMemberTable.add(
          FlightMemberSchema(
              userId = role.assignedUser?.id, flightId = flightId, roleType = role.roleType),
          { delayedCallback.run() },
          onError)
    }
  }

  /**
   * Retrieve the team members of the flight
   *
   * First query the flight-member relation then for the roles which have a user assigned, query the
   * user table.
   */
  private fun retrieveTeam(
      schema: FlightSchema,
      onCompletion: (Team) -> Unit,
      onError: (Exception) -> Unit
  ) {
    flightMemberTable.query(
        Filter.equalTo("flightId", schema.id),
        { members ->
          val roles = mutableListOf<Role>()
          val numUserRequests = members.filter { it.userId != null }.size

          // in case no user is assigned yet, do not rely on the delayed callback
          // as it would be run immediately (see doc) returning an empty list of roles
          // which is not the case in general
          if (numUserRequests == 0) {
            onCompletion(Team(members.map { Role(it.roleType!!, null) }))
            return@query
          }
          val delayedCallback =
              ParallelOperationsEndCallback(numUserRequests) { onCompletion(Team(roles)) }
          for (member in members) {
            if (member.userId == null) {
              roles.add(Role(member.roleType!!, null))
              continue
            }
            userTable.get(
                member.userId,
                {
                  if (it == null) {
                    // report
                  } else {
                    roles.add(Role(member.roleType!!, it))
                    delayedCallback.run()
                  }
                },
                onError)
          }
        },
        onError)
  }

  /** Retrieve the vehicles linked to the flight */
  private fun retrieveVehicles(
      schema: FlightSchema,
      onCompletion: (List<Vehicle>) -> Unit,
      onError: (Exception) -> Unit
  ) {
    var vehicles = mutableListOf<Vehicle>()
    var delayedCallback =
        ParallelOperationsEndCallback(schema.vehicleIds!!.size) { onCompletion(vehicles) }
    for (vehicleId in schema.vehicleIds!!) {
      vehicleTable.get(
          vehicleId,
          {
            if (it == null) {
              // report
            } else {
              vehicles.add(it)
              delayedCallback.run()
            }
          },
          onError)
    }
  }

  /** Retrieve all the entities linked to the flight */
  private fun retrieveFlight(
      flightSchema: FlightSchema,
      onCompletion: (Flight?) -> Unit,
      onError: (Exception) -> Unit
  ) {
    var schema = flightSchema
    var flightType: FlightType? = null
    var balloon: Balloon? = null
    var basket: Basket? = null
    var vehicles: List<Vehicle>? = null
    var team: Team? = null

    // the number of requests that will be executed depends
    // on if balloon/basket IDs are defined
    var numEntitiesRequests = 3
    if (flightSchema.balloonId != null) numEntitiesRequests += 1
    if (flightSchema.basketId != null) numEntitiesRequests += 1

    val delayedOnCompletion =
        ParallelOperationsEndCallback(numEntitiesRequests) {
          onCompletion(makeFlight(schema, flightType!!, balloon, basket, vehicles!!, team!!))
        }
    flightTypeTable.get(
        schema.flightTypeId!!,
        {
          if (it == null) {
            onCompletion(null)
          } else {
            flightType = it
            delayedOnCompletion.run()
          }
        },
        onError)

    if (flightSchema.balloonId != null) {
      balloonTable.get(
          schema.balloonId!!,
          {
            if (it == null) {
              onCompletion(null)
            } else {
              balloon = it
              delayedOnCompletion.run()
            }
          },
          onError)
    }

    if (flightSchema.basketId != null) {
      basketTable.get(
          schema.basketId!!,
          {
            if (it == null) {
              onCompletion(null)
            } else {
              basket = it
              delayedOnCompletion.run()
            }
          },
          onError)
    }

    retrieveVehicles(
        schema,
        {
          vehicles = it
          delayedOnCompletion.run()
        },
        onError)

    retrieveTeam(
        schema,
        {
          team = it
          delayedOnCompletion.run()
        },
        onError)
  }

  override fun get(id: String, onCompletion: (Flight?) -> Unit, onError: (Exception) -> Unit) {
    db.getItem(
        path,
        id,
        clazz,
        { schema ->
          if (schema == null) {
            onCompletion(null)
          } else {
            retrieveFlight(schema, onCompletion, onError)
          }
        },
        onError)
  }

  override fun getAll(onCompletion: (List<Flight>) -> Unit, onError: (Exception) -> Unit) {
    db.getAll(
        path,
        clazz,
        { schemas ->
          val flights = mutableListOf<Flight>()
          val delayedCallback =
              ParallelOperationsEndCallback(schemas.size) { onCompletion(flights) }
          for (schema in schemas) {
            retrieveFlight(
                schema,
                {
                  if (it == null) {
                    // report
                  } else {
                    flights.add(it)
                    delayedCallback.run()
                  }
                },
                onError)
          }
        },
        onError)
  }

  override fun query(
      filter: Filter,
      onCompletion: (List<Flight>) -> Unit,
      onError: (Exception) -> Unit
  ) {
    db.query(
        path,
        filter,
        clazz,
        { schemas ->
          val flights = mutableListOf<Flight>()
          val delayedCallback =
              ParallelOperationsEndCallback(schemas.size) { onCompletion(flights) }
          for (schema in schemas) {
            retrieveFlight(
                schema,
                {
                  if (it == null) {
                    // report
                  } else {
                    flights.add(it)
                    delayedCallback.run()
                  }
                },
                onError)
          }
        },
        onError)
  }

  /**
   * Add a new flight to the database
   *
   * This will generate a new id for this flight and disregard any previously set id. This will
   * create the [Flight.team] (and in the process the [User.assignedFlights])
   *
   * @param item The flight to add to the database
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  fun add(item: Flight, onCompletion: (id: String) -> Unit, onError: (Exception) -> Unit) {
    db.addItem(
        path,
        FlightSchema.fromModel(item),
        { flightId -> addTeam(flightId, item.team, { onCompletion(flightId) }, onError) },
        onError)
  }

  override fun delete(
      id: String,
      onCompletion: () -> Unit,
      onError: (java.lang.Exception) -> Unit
  ) {
    flightMemberTable.queryDelete(
        Filter.equalTo("flightId", id), { super.delete(id, onCompletion, onError) }, onError)
  }

  companion object {
    const val PATH = "flight"
  }
}
