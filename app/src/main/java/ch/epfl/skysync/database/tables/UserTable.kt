package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.ParallelOperationsEndCallback
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.UserSchema
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.user.User
import com.google.firebase.firestore.Filter

/** Represent the "user" table */
class UserTable(db: FirestoreDatabase) : Table<User, UserSchema>(db, UserSchema::class, PATH) {
  private val availabilityTable = AvailabilityTable(db)
  private val flightMemberTable = FlightMemberTable(db)

  /** Retrieve and set all availabilities linked to the user */
  private fun retrieveAvailabilities(
      user: User,
      onCompletion: (User?) -> Unit,
      onError: (Exception) -> Unit
  ) {
    availabilityTable.query(
        Filter.equalTo("userId", user.id),
        { availabilities ->
          user.availabilities.addCells(availabilities)
          onCompletion(user)
        },
        onError)
  }

  /** Delete all availabilities linked to the user */
  private fun deleteAvailabilities(
      id: String,
      onCompletion: () -> Unit,
      onError: (Exception) -> Unit
  ) {
    availabilityTable.queryDelete(Filter.equalTo("userId", id), onCompletion, onError)
  }

  /**
   * Retrieve all assigned flights linked to a user
   *
   * @param flightTable The flight table is passed as dependency injection to prevent a circular
   *   reference between [FlightTable] and [UserTable]
   * @param id The id of the user
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  fun retrieveAssignedFlights(
      flightTable: FlightTable,
      id: String,
      onCompletion: (List<Flight>) -> Unit,
      onError: (Exception) -> Unit
  ) {
    flightMemberTable.query(
        Filter.equalTo("userId", id),
        { memberships ->
          var flights = mutableListOf<Flight>()
          val delayedCallback =
              ParallelOperationsEndCallback(memberships.size) { onCompletion(flights) }
          for (membership in memberships) {
            flightTable.get(
                membership.flightId!!,
                { flight ->
                  if (flight == null) {
                    // report
                  } else {
                    flights.add(flight)
                  }
                  delayedCallback.run()
                },
                onError)
          }
        },
        onError)
  }

  /** Remove user from all its flight assignments */
  private fun removeUserFromFlightMemberSchemas(
      id: String,
      onCompletion: () -> Unit,
      onError: (Exception) -> Unit
  ) {
    flightMemberTable.query(
        Filter.equalTo("userId", id),
        { memberships ->
          val delayedCallback = ParallelOperationsEndCallback(memberships.size) { onCompletion() }
          for (membership in memberships) {
            flightMemberTable.update(
                membership.id!!, membership.copy(userId = null), { delayedCallback.run() }, onError)
          }
        },
        onError)
  }

  /**
   * Get an user by ID
   *
   * This will get the user and its availabilities but not its assigned flights, this has to be done
   * separately using [retrieveAssignedFlights].
   *
   * @param id The id of the user
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  override fun get(id: String, onCompletion: (User?) -> Unit, onError: (Exception) -> Unit) {
    super.get(
        id,
        { user ->
          if (user == null) {
            onCompletion(null)
          } else {
            retrieveAvailabilities(user, { onCompletion(it) }, onError)
          }
        },
        onError)
  }

  override fun getAll(onCompletion: (List<User>) -> Unit, onError: (Exception) -> Unit) {
    super.getAll(
        { users ->
          val delayedCallback = ParallelOperationsEndCallback(users.size) { onCompletion(users) }
          for (user in users) {
            retrieveAvailabilities(user, { delayedCallback.run() }, onError)
          }
        },
        onError)
  }

  override fun query(
      filter: Filter,
      onCompletion: (List<User>) -> Unit,
      onError: (Exception) -> Unit
  ) {
    super.query(
        filter,
        { users ->
          val delayedCallback = ParallelOperationsEndCallback(users.size) { onCompletion(users) }
          for (user in users) {
            retrieveAvailabilities(user, { delayedCallback.run() }, onError)
          }
        },
        onError)
  }

  override fun delete(
      id: String,
      onCompletion: () -> Unit,
      onError: (java.lang.Exception) -> Unit
  ) {
    val delayedCallback = ParallelOperationsEndCallback(3) { onCompletion() }
    super.delete(id, { delayedCallback.run() }, onError)
    deleteAvailabilities(id, { delayedCallback.run() }, onError)
    removeUserFromFlightMemberSchemas(id, { delayedCallback.run() }, onError)
  }

  /**
   * Set a new user to the database
   *
   * Set item at id and override any previously set id. This will not add availabilities or assigned
   * flights to the database, it must be done separately.
   *
   * @param item The user to add to the database
   * @param id the id of the item (if null a new id is generated)
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  fun set(id: String, item: User, onCompletion: () -> Unit, onError: (Exception) -> Unit) {
    db.setItem(path, id, UserSchema.fromModel(item), onCompletion, onError)
  }

  companion object {
    const val PATH = "user"
  }
}
