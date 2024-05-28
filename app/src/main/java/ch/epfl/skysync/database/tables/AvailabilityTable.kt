package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.DateUtility
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.AvailabilitySchema
import ch.epfl.skysync.models.calendar.Availability
import ch.epfl.skysync.models.calendar.TimeSlot
import com.google.firebase.firestore.Filter
import java.time.LocalDate
import kotlinx.coroutines.coroutineScope

/** Represent the "availability" table */
class AvailabilityTable(db: FirestoreDatabase) :
    Table<Availability, AvailabilitySchema>(db, AvailabilitySchema::class, PATH) {

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

  suspend fun queryByDateAndUserId(
      userId: String,
      localDate: LocalDate,
      timeslot: TimeSlot,
      onError: ((Exception) -> Unit)? = null
  ): Availability = coroutineScope {
    withErrorCallback(onError) {
      val dateFilter = Filter.equalTo("date", DateUtility.localDateToDate(localDate))
      val timeslotFilter = Filter.equalTo("timeSlot", timeslot)
      val userFilter = Filter.equalTo("userId", userId)
      val availabilityFilter = Filter.and(dateFilter, timeslotFilter, userFilter)

      val availabilities = query(availabilityFilter)
      availabilities[0]
    }
  }

  companion object {
    const val PATH = "availability"
  }
}
