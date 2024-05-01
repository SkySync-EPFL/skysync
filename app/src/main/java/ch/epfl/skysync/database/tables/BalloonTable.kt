package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.DateLocalDateConverter
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.BalloonSchema
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.Flight
import com.google.firebase.firestore.Filter
import java.time.LocalDate

/** Represent the "balloon" table */
class BalloonTable(db: FirestoreDatabase) :
    Table<Balloon, BalloonSchema>(db, BalloonSchema::class, PATH) {

  /**
   * Add a new balloon to the database
   *
   * @param item The balloon to add to the database
   * @param onError Callback called when an error occurs
   */
  suspend fun add(item: Balloon, onError: ((Exception) -> Unit)? = null): String {
    return withErrorCallback(onError) { db.addItem(path, BalloonSchema.fromModel(item)) }
  }

  /**
   * Returns the available balloons on a given date and timeslot
   *
   * @param localDate The requested day
   * @param timeslot The requested timeslot
   * @param onError Callback called when an error occurs
   */
  suspend fun getBalloonsAvailableOn(
      flightTable: FlightTable,
      localDate: LocalDate,
      timeslot: TimeSlot,
      onError: ((Exception) -> Unit)?
  ): List<Balloon> {
    val dateFilter = Filter.equalTo("date", DateLocalDateConverter.localDateToDate(localDate))
    val timeslotFilter = Filter.equalTo("timeSlot", timeslot)
    val flightFilter = Filter.and(dateFilter, timeslotFilter)

    val unavailableBalloonIds: Set<String> =
        flightTable
            .query(flightFilter, onError)
            .mapNotNull { flight: Flight -> flight.balloon?.id }
            .toSet()

    return getAll(onError).filterNot { balloon: Balloon -> balloon.id in unavailableBalloonIds }
  }

  companion object {
    const val PATH = "balloon"
  }
}
