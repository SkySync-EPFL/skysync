package ch.epfl.skysync.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.user.User
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QueriesTest {

  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  private val queries = Queries(db)

  @Before
  fun testSetup() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
  }

  @Test
  fun getBasketsAvailableOnTest() = runTest {
    var availabilityBaskets =
        queries.getBasketsAvailableOn(dbs.date1, TimeSlot.AM, onError = { Assert.assertNull(it) })
    Assert.assertEquals(listOf(dbs.basket3), availabilityBaskets)

    availabilityBaskets =
        queries.getBasketsAvailableOn(dbs.date1, TimeSlot.PM, onError = { Assert.assertNull(it) })

    Assert.assertEquals(
        listOf(dbs.basket2, dbs.basket3).sortedBy { basket: Basket -> basket.id },
        availabilityBaskets.sortedBy { basket: Basket -> basket.id })

    availabilityBaskets =
        queries.getBasketsAvailableOn(
            dbs.dateNoFlight, TimeSlot.AM, onError = { Assert.assertNull(it) })

    Assert.assertEquals(
        listOf(dbs.basket1, dbs.basket2, dbs.basket3).sortedBy { basket: Basket -> basket.id },
        availabilityBaskets.sortedBy { basket: Basket -> basket.id })
  }

  @Test
  fun getBalloonsAvailableOnTest() = runTest {
    var availabilityBalloons =
        queries.getBalloonsAvailableOn(dbs.date1, TimeSlot.AM, onError = { Assert.assertNull(it) })
    Assert.assertEquals(listOf(dbs.balloon3), availabilityBalloons)

    availabilityBalloons =
        queries.getBalloonsAvailableOn(dbs.date1, TimeSlot.PM, onError = { Assert.assertNull(it) })

    Assert.assertEquals(
        listOf(dbs.balloon1, dbs.balloon3).sortedBy { balloon: Balloon -> balloon.id },
        availabilityBalloons.sortedBy { balloon: Balloon -> balloon.id })

    availabilityBalloons =
        queries.getBalloonsAvailableOn(
            dbs.dateNoFlight, TimeSlot.AM, onError = { Assert.assertNull(it) })

    Assert.assertEquals(
        listOf(dbs.balloon1, dbs.balloon2, dbs.balloon3).sortedBy { balloon: Balloon ->
          balloon.id
        },
        availabilityBalloons.sortedBy { balloon: Balloon -> balloon.id })
  }

  @Test
  fun getUsersAvailableOnTest() = runTest {
    var availableUsers =
        queries.getUsersAvailableOn(dbs.date1, TimeSlot.AM, onError = { Assert.assertNull(it) })

    Assert.assertEquals(listOf<User>(), availableUsers)

    availableUsers =
        queries.getUsersAvailableOn(dbs.date1, TimeSlot.PM, onError = { Assert.assertNull(it) })

    Assert.assertEquals(listOf(dbs.crew2), availableUsers)

    availableUsers =
        queries.getUsersAvailableOn(
            dbs.dateNoFlight, TimeSlot.AM, onError = { Assert.assertNull(it) })

    Assert.assertEquals(listOf(dbs.pilot2), availableUsers)
  }

  @Test
  fun getFlightsForUser() = runTest {
    var allflights = queries.getFlightsForUser(dbs.pilot1.id, onError = { Assert.assertNull(it) })
    Assert.assertEquals(
        listOf(dbs.flight1, dbs.flight3).sortedBy { f -> f.id }, allflights.sortedBy { f -> f.id })

    allflights = queries.getFlightsForUser(dbs.crew2.id, onError = { Assert.assertNull(it) })
    Assert.assertEquals(listOf(dbs.flight2), allflights)

    allflights = queries.getFlightsForUser(dbs.admin1.id, onError = { Assert.assertNull(it) })
    Assert.assertEquals(listOf<Flight>(), allflights)
  }
}
