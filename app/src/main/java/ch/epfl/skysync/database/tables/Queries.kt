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
   */
  suspend fun getBasketsAvailableOn(localDate: LocalDate, timeslot: TimeSlot): List<Basket> {
    val dateFilter = Filter.equalTo("date", DateLocalDateConverter.localDateToDate(localDate))
    val timeslotFilter = Filter.equalTo("timeSlot", timeslot)
    val flightFilter = Filter.and(dateFilter, timeslotFilter)

    val unavailableBasketsIds: Set<String> =
        flightTable.query(flightFilter).mapNotNull { flight: Flight -> flight.basket?.id }.toSet()

    val allBaskets = basketTable.getAll()

    return allBaskets.filterNot { basket: Basket -> basket.id in unavailableBasketsIds }
  }

  /**
   * Returns the available balloons on a given date and timeslot
   *
   * @param localDate The requested day
   * @param timeslot The requested timeslot
   */
  suspend fun getBalloonsAvailableOn(localDate: LocalDate, timeslot: TimeSlot): List<Balloon> {
    val dateFilter = Filter.equalTo("date", DateLocalDateConverter.localDateToDate(localDate))
    val timeslotFilter = Filter.equalTo("timeSlot", timeslot)
    val flightFilter = Filter.and(dateFilter, timeslotFilter)

    val unavailableBalloonIds: Set<String> =
        flightTable.query(flightFilter).mapNotNull { flight: Flight -> flight.balloon?.id }.toSet()

    val allBalloons = balloonTable.getAll()

    return allBalloons.filterNot { balloon: Balloon -> balloon.id in unavailableBalloonIds }
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
            flightMemberTable.query(Filter.inArray("flightId", flightsIds)).map { fm -> fm.userId }
        // Retrieve all possible available members
        val potentialAvailableUsers =
            userTable.query(Filter.notInArray(FieldPath.documentId(), unavailableUserIds))

        val availableUsers = mutableListOf<User>()
        val jobs = mutableListOf<Job>()
        // For each potential user check if he is available on the given day
        for (user in potentialAvailableUsers) {
          jobs.add(
              launch {
                val a =
                    availabilityTable.query(
                        Filter.and(Filter.equalTo("userId", user.id), dateTimeSlotFilter))
                // There is only 1 availability
                if (a.isNotEmpty() && a[0].status == AvailabilityStatus.OK) {
                  availableUsers.add(user)
                }
              })
        }
        jobs.forEach { it.join() }
        return@coroutineScope availableUsers
      }

  /**
   * Returns the all the flights of a given user
   *
   * @param id The id of the user
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
