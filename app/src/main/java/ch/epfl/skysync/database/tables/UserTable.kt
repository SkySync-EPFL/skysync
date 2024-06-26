package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.DateUtility
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.UserSchema
import ch.epfl.skysync.models.calendar.Availability
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.user.User
import com.google.firebase.firestore.Filter
import java.time.LocalDate
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Represents the "user" table in the database.
 *
 * @property db The FirestoreDatabase instance for interacting with the Firestore database.
 */
class UserTable(db: FirestoreDatabase) : Table<User, UserSchema>(db, UserSchema::class, PATH) {
  private val availabilityTable = AvailabilityTable(db)
  private val flightMemberTable = FlightMemberTable(db)
  private val tempUserTable = TempUserTable(db)

  /**
   * Retrieve and set all availabilities linked to the user
   *
   * @param id The id of the user
   * @param onError Callback called when an error occurs
   */
  suspend fun retrieveAvailabilities(
      id: String,
      onError: ((Exception) -> Unit)? = null
  ): List<Availability> {
    return availabilityTable.query(Filter.equalTo("userId", id), onError = onError)
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

  /**
   * Removes the user from all its flight assignments.
   *
   * This function will go through all the flight-member relations where the user is a member, and
   * set the user ID to null, effectively removing the user from these flights.
   *
   * @param id The ID of the user to be removed from flight assignments.
   */
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
   * This will only get the user:
   * - to get its assigned flights, use [retrieveAssignedFlights].
   * - to get its availabilities, use [retrieveAvailabilities].
   *
   * @param id The id of the user
   * @param onError Callback called when an error occurs
   */
  override suspend fun get(id: String, onError: ((Exception) -> Unit)?): User? {
    return super.get(id, onError = onError)
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
   * Update the user at the given id.
   *
   * @param item The user to update
   * @param id The id of the user
   * @param onError Callback called when an error occurs
   */
  suspend fun update(id: String, item: User, onError: ((Exception) -> Unit)? = null) {
    return withErrorCallback(onError) { db.setItem(path, id, UserSchema.fromModel(item)) }
  }

  /**
   * Returns the available user on the given day and timeslot
   *
   * @param localDate The requested day
   * @param timeslot The requested timeslot
   * @param onError Callback called when an error occurs
   */
  suspend fun getUsersAvailableOn(
      flightTable: FlightTable,
      localDate: LocalDate,
      timeslot: TimeSlot,
      onError: ((Exception) -> Unit)?
  ): List<User> = coroutineScope {
    withErrorCallback(onError) {
      val dateFilter = Filter.equalTo("date", DateUtility.localDateToDate(localDate))
      val timeslotFilter = Filter.equalTo("timeSlot", timeslot)
      val dateTimeSlotFilter = Filter.and(dateFilter, timeslotFilter)

      // Retrieve all flights of the given day, timeslot
      val flightsIds = flightTable.query(dateTimeSlotFilter).map { flight: Flight -> flight.id }

      // Retrieve all possible available members
      var potentialAvailableUsers = getAll()

      // If there are flights on the given day
      // Retrieve all members of these flights (they are not available)
      if (flightsIds.isNotEmpty()) {
        val unavailableUserIds =
            flightMemberTable.query(Filter.inArray("flightId", flightsIds)).map { fm -> fm.userId }
        // Remove these user from the possible available members
        // Now there are only user left who can be available on the given day and timeslot
        potentialAvailableUsers =
            potentialAvailableUsers.filterNot { user: User -> user.id in unavailableUserIds }
      }
      // Return all user who are available on the given day and timeslot
      potentialAvailableUsers
          .map { user: User ->
            async {
              val availableUsers =
                  availabilityTable.query(
                      Filter.and(Filter.equalTo("userId", user.id), dateTimeSlotFilter))

              if (availableUsers.firstOrNull()?.status == AvailabilityStatus.OK) {
                return@async user
              }
              return@async null
            }
          }
          .awaitAll()
          .filterNotNull()
    }
  }

  companion object {
    const val PATH = "user"
  }
}
