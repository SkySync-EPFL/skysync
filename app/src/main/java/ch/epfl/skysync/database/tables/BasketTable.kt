package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.DateLocalDateConverter
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.BasketSchema
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.Flight
import com.google.firebase.firestore.Filter
import java.time.LocalDate
import kotlinx.coroutines.coroutineScope

/** Represent the "basket" table */
class BasketTable(db: FirestoreDatabase) :
    Table<Basket, BasketSchema>(db, BasketSchema::class, PATH) {

  /**
   * Add a new basket to the database
   *
   * @param item The basket to add to the database
   * @param onError Callback called when an error occurs
   */
  suspend fun add(item: Basket, onError: ((Exception) -> Unit)? = null): String {
    return withErrorCallback(onError) { db.addItem(path, BasketSchema.fromModel(item)) }
  }

  /**
   * Returns the available baskets on a given date and timeslot
   *
   * @param localDate The requested day
   * @param timeslot The requested timeslot
   * @param onError Callback called when an error occurs
   */
  suspend fun getBasketsAvailableOn(
      flightTable: FlightTable,
      localDate: LocalDate,
      timeslot: TimeSlot,
      onError: ((Exception) -> Unit)?
  ): List<Basket> = coroutineScope {
    withErrorCallback(onError) {
      val dateFilter = Filter.equalTo("date", DateLocalDateConverter.localDateToDate(localDate))
      val timeslotFilter = Filter.equalTo("timeSlot", timeslot)
      val flightFilter = Filter.and(dateFilter, timeslotFilter)

      val unavailableBasketsIds: Set<String> =
          flightTable.query(flightFilter).mapNotNull { flight: Flight -> flight.basket?.id }.toSet()

      getAll().filterNot { basket: Basket -> basket.id in unavailableBasketsIds }
    }
  }

  companion object {
    const val PATH = "basket"
  }
}
