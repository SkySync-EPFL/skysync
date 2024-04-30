package ch.epfl.skysync.database

import ch.epfl.skysync.database.tables.AvailabilityTable
import ch.epfl.skysync.database.tables.BalloonTable
import ch.epfl.skysync.database.tables.BasketTable
import ch.epfl.skysync.database.tables.FlightMemberTable
import ch.epfl.skysync.database.tables.FlightTable
import ch.epfl.skysync.database.tables.UserTable
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.user.User
import com.google.firebase.firestore.Filter
import java.time.LocalDate
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class Queries(db: FirestoreDatabase) {
  private val flightTable = FlightTable(db)
  private val basketTable = BasketTable(db)
  private val flightMemberTable = FlightMemberTable(db)
  private val userTable = UserTable(db)
  private val availabilityTable = AvailabilityTable(db)
  private val balloonTable = BalloonTable(db)

  /**
   * Returns the available baskets on a given date and timeslot
   *
   * @param localDate The requested day
   * @param timeslot The requested timeslot
   * @param onError Callback called when an error occurs
   */
  suspend fun getBasketsAvailableOn(
      localDate: LocalDate,
      timeslot: TimeSlot,
      onError: ((Exception) -> Unit)?
  ): List<Basket> {
    val dateFilter = Filter.equalTo("date", DateLocalDateConverter.localDateToDate(localDate))
    val timeslotFilter = Filter.equalTo("timeSlot", timeslot)
    val flightFilter = Filter.and(dateFilter, timeslotFilter)

    val unavailableBasketsIds: Set<String> =
        flightTable
            .query(flightFilter, onError)
            .mapNotNull { flight: Flight -> flight.basket?.id }
            .toSet()

    return basketTable.getAll(onError).filterNot { basket: Basket ->
      basket.id in unavailableBasketsIds
    }
  }

  /**
   * Returns the available balloons on a given date and timeslot
   *
   * @param localDate The requested day
   * @param timeslot The requested timeslot
   * @param onError Callback called when an error occurs
   */
  suspend fun getBalloonsAvailableOn(
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

    return balloonTable.getAll(onError).filterNot { balloon: Balloon ->
      balloon.id in unavailableBalloonIds
    }
  }

  /**
   * Returns the available user on the given day and timeslot
   *
   * @param localDate The requested day
   * @param timeslot The requested timeslot
   * @param onError Callback called when an error occurs
   */
  suspend fun getUsersAvailableOn(
      localDate: LocalDate,
      timeslot: TimeSlot,
      onError: ((Exception) -> Unit)?
  ): List<User> = coroutineScope {
    val dateFilter = Filter.equalTo("date", DateLocalDateConverter.localDateToDate(localDate))
    val timeslotFilter = Filter.equalTo("timeSlot", timeslot)
    val dateTimeSlotFilter = Filter.and(dateFilter, timeslotFilter)

    // Retrieve all flights of the given day, timeslot
    val flightsIds =
        flightTable.query(dateTimeSlotFilter, onError).map { flight: Flight -> flight.id }

    // Retrieve all possible available members
    var potentialAvailableUsers = userTable.getAll(onError)

    // If there are flights on the given day
    // Retrieve all members of these flights (they are not available)
    if (flightsIds.isNotEmpty()) {
      val unavailableUserIds =
          flightMemberTable.query(Filter.inArray("flightId", flightsIds), onError).map { fm ->
            fm.userId
          }
      // Remove these user from the possible available members
      // Now there are only user left who can be available on the given day and timeslot
      potentialAvailableUsers =
          potentialAvailableUsers.filterNot { user: User -> user.id in unavailableUserIds }
    }
    // Return all user who are available on the given day and timeslot
    return@coroutineScope potentialAvailableUsers
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

  /**
   * Returns the all the flights of a given user
   *
   * @param id The id of the user
   * @param onError Callback called when an error occurs
   */
  suspend fun getFlightsForUser(id: String, onError: ((Exception) -> Unit)?): List<Flight> =
      coroutineScope {
        val flightIds =
            flightMemberTable.query(Filter.equalTo("userId", id)).map { flightMemberSchema ->
              flightMemberSchema.flightId!!
            }

        return@coroutineScope flightIds
            .map { id -> async { flightTable.get(id) } }
            .awaitAll()
            .filterNotNull()
      }
}
