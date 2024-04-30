package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.DateLocalDateConverter
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.user.User
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Filter
import java.time.LocalDate
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

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

    val allBaskets = basketTable.getAll(onError)

    return allBaskets.filterNot { basket: Basket -> basket.id in unavailableBasketsIds }
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

    val allBalloons = balloonTable.getAll(onError)

    return allBalloons.filterNot { balloon: Balloon -> balloon.id in unavailableBalloonIds }
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
    // Retrieve all members of these flights (they are not available)
    val unavailableUserIds =
        flightMemberTable.query(Filter.inArray("flightId", flightsIds), onError).map { fm -> fm.userId }
    // Retrieve all possible available members
    val potentialAvailableUsers = userTable.getAll(onError).filterNot { user: User -> user.id in unavailableUserIds }

      return@coroutineScope  potentialAvailableUsers
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
            .awaitAll().filterNotNull()
  }

  /**
   * Returns the all the flights of a given user
   *
   * @param id The id of the user
   * @param onError Callback called when an error occurs
   */
  suspend fun getFlightsForUser(id: String, onError: ((Exception) -> Unit)?): List<Flight?> =
      coroutineScope {
        val flightIds =
            flightMemberTable.query(Filter.equalTo("userId", id)).map { flightMemberSchema ->
              flightMemberSchema.flightId!!
            }

        return@coroutineScope flightIds.map { id -> async { flightTable.get(id) } }.awaitAll()
      }
}
