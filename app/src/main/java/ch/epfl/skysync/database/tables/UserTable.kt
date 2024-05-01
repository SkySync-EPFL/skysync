package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.UserSchema
import ch.epfl.skysync.models.calendar.Availability
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.user.User
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Query
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/** Represent the "user" table */
class UserTable(db: FirestoreDatabase) : Table<User, UserSchema>(db, UserSchema::class, PATH) {
  private val availabilityTable = AvailabilityTable(db)
  private val flightMemberTable = FlightMemberTable(db)
  private val tempUserTable = TempUserTable(db)

  /** Retrieve and set all availabilities linked to the user */
  private suspend fun retrieveAvailabilities(userId: String): List<Availability> {
    return availabilityTable.query(Filter.equalTo("userId", userId))
  }

  /** Delete all availabilities linked to the user */
  private suspend fun deleteAvailabilities(id: String) {
    availabilityTable.queryDelete(Filter.equalTo("userId", id))
  }

  /**
   * Retrieve all assigned flights linked to a user
   *
   * @param flightTable The flight table is passed as dependency injection to prevent a circular
   *   reference between [FlightTable] and [UserTable]
   * @param id The id of the user
   * @param onError Callback called when an error occurs
   */
  suspend fun retrieveAssignedFlights(
      flightTable: FlightTable,
      id: String,
      onError: ((Exception) -> Unit)? = null
  ): List<Flight> = coroutineScope {
    withErrorCallback(onError) {
      val memberships = flightMemberTable.query(Filter.equalTo("userId", id), onError = null)
      val flights =
          memberships
              .map { membership ->
                async {
                  val flight = flightTable.get(membership.flightId!!)
                  if (flight == null) {
                    // report
                  }
                  flight
                }
              }
              .awaitAll()
      flights.filterNotNull()
    }
  }

  /** Remove user from all its flight assignments */
  private suspend fun removeUserFromFlightMemberSchemas(id: String): Unit = coroutineScope {
    val memberships = flightMemberTable.query(Filter.equalTo("userId", id))
    memberships
        .map { membership ->
          async { flightMemberTable.update(membership.id!!, membership.copy(userId = null)) }
        }
        .awaitAll()
  }

  /**
   * Get an user by ID
   *
   * This will get the user and its availabilities but not its assigned flights, this has to be done
   * separately using [retrieveAssignedFlights].
   *
   * @param id The id of the user
   * @param onError Callback called when an error occurs
   */
  override suspend fun get(id: String, onError: ((Exception) -> Unit)?): User? {
    return withErrorCallback(onError) {
      val user = super.get(id, onError = null) ?: return@withErrorCallback null
      user.availabilities.addCells(retrieveAvailabilities(user.id))
      user
    }
  }

  override suspend fun getAll(onError: ((Exception) -> Unit)?): List<User> = coroutineScope {
    withErrorCallback(onError) {
      val users = super.getAll(onError = null)
      users
          .map { user -> async { user.availabilities.addCells(retrieveAvailabilities(user.id)) } }
          .awaitAll()
      users
    }
  }

  override suspend fun query(
      filter: Filter,
      limit: Long?,
      orderBy: String?,
      orderByDirection: Query.Direction,
      onError: ((Exception) -> Unit)?
  ): List<User> = coroutineScope {
    withErrorCallback(onError) {
      val users = super.query(filter, limit, orderBy, orderByDirection, onError = null)
      users
          .map { user -> async { user.availabilities.addCells(retrieveAvailabilities(user.id)) } }
          .awaitAll()
      users
    }
  }

  override suspend fun delete(id: String, onError: ((Exception) -> Unit)?): Unit = coroutineScope {
    withErrorCallback(onError) {
      listOf(
              async { super.delete(id, onError = null) },
              async { deleteAvailabilities(id) },
              async { removeUserFromFlightMemberSchemas(id) },
          )
          .awaitAll()
    }
  }

  /**
   * Set a new user to the database
   *
   * Set item at id and override any previously set id. This will not add availabilities or assigned
   * flights to the database, it must be done separately.
   *
   * @param item The user to add to the database
   * @param id The id of the user
   * @param onError Callback called when an error occurs
   */
  suspend fun set(id: String, item: User, onError: ((Exception) -> Unit)? = null) {
    return withErrorCallback(onError) { db.setItem(path, id, UserSchema.fromModel(item)) }
  }

  /**
   * Update a user
   *
   * Update the user at the given id.
   *
   * @param item The user to update
   * @param id The id of the user
   * @param onError Callback called when an error occurs
   */
  suspend fun update(id: String, item: User, onError: ((Exception) -> Unit)? = null) {
    return withErrorCallback(onError) { db.setItem(path, id, UserSchema.fromModel(item)) }
  }

    /*
  /**
   * Creates a new User if the entry exists in the temporary user table
   *
   * @param id The id of the user
   * @param email The email of the user (used as key in tempUserTable)
   * @param onError Callback called when an error occurs
   */
  suspend fun createUser(id: String, email: String, onError: ((Exception) -> Unit)? = null) {
    return withErrorCallback(onError) {
      val tempUser = tempUserTable.get(email)
      if (tempUser != null) {
        set(id, tempUser.toUserSchema(id).toModel())
        tempUserTable.delete(email)
      } else {
        /*
        val u = TempUser(
            email = email,
            userRole = UserRole.CREW,
            firstname = "Jean",
            lastname = "François",
            balloonQualification = null
        )
        tempUserTable.set(email, u)
        */
      }
    }
  }
  */

  companion object {
    const val PATH = "user"
  }
}
