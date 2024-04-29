package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.DateLocalDateConverter
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.AvailabilitySchema
import ch.epfl.skysync.models.calendar.Availability
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.user.User
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Filter
import java.time.LocalDate
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/** Represent the "availability" table */
class AvailabilityTable(db: FirestoreDatabase) :
    Table<Availability, AvailabilitySchema>(db, AvailabilitySchema::class, PATH) {

  private val userTable = UserTable(db)
  private val flightTable = FlightTable(db)
  private val flightMemberTable = FlightMemberTable(db)

  /**
   * Add a new availability to the database
   *
   * This will generate a new id for this availability and disregard any previously set id.
   *
   * @param userId The ID of the user whose availability it is
   * @param item The availability to add to the database
   * @param onError Callback called when an error occurs
   */
  suspend fun add(
      userId: String,
      item: Availability,
      onError: ((Exception) -> Unit)? = null
  ): String {
    return withErrorCallback(onError) {
      db.addItem(path, AvailabilitySchema.fromModel(userId, item))
    }
  }

  /**
   * Update a availability
   *
   * This will overwrite the availability at the given id.
   *
   * @param userId The ID of the user whose availability it is
   * @param availabilityId The id of the availability to update
   * @param item The new availability item
   * @param onError Callback called when an error occurs
   */
  suspend fun update(
      userId: String,
      availabilityId: String,
      item: Availability,
      onError: ((Exception) -> Unit)? = null
  ) {
    return withErrorCallback(onError) {
      db.setItem(path, availabilityId, AvailabilitySchema.fromModel(userId, item))
    }
  }
  /**
   * Returns the available user on the given day and timeslot
   *
   * @param localDate The requested day
   * @param timeslot The requested timeslot
   */
  suspend fun getUsersAvailableOn(localDate: LocalDate, timeslot: TimeSlot): List<User> =
      coroutineScope {
        val dateFilter = Filter.equalTo("date", DateLocalDateConverter.localDateToDate(localDate))
        val timeslotFilter = Filter.equalTo("timeSlot", timeslot)
        val dateTimeSlotFilter = Filter.and(dateFilter, timeslotFilter)

        // Retrieve all flights of the given day, timeslot
        val flightsIds = flightTable.query(dateTimeSlotFilter).map { flight: Flight -> flight.id }
        // Retrieve all members of these flights (they are not available)
        val unavailableUserIds =
            flightMemberTable.query(Filter.arrayContainsAny("flightId", flightsIds)).map { fm ->
              fm.userId
            }
        // Retrieve all possible available members
        val potentialAvailableUsers =
            userTable.query(Filter.notInArray(FieldPath.documentId(), unavailableUserIds))

        val availableUsers = mutableListOf<User>()
        val jobs = mutableListOf<Job>()
        // For each potential user check if he is available on the given day
        for (user in potentialAvailableUsers) {
          jobs.add(
              launch {
                val a = query(Filter.and(Filter.equalTo("userId", user.id), dateTimeSlotFilter))
                // There is only 1 availability
                if (a.isNotEmpty() && a[0].status == AvailabilityStatus.OK) {
                  availableUsers.add(user)
                }
              })
        }
        jobs.forEach { it.join() }
        return@coroutineScope availableUsers
      }

  companion object {
    const val PATH = "availability"
  }
}
