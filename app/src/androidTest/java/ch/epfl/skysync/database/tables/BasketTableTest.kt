package ch.epfl.skysync.tables

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.tables.BasketTable
import ch.epfl.skysync.database.tables.FlightTable
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Basket
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BasketTableTest {

  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  private val basketTable = BasketTable(db)
  private val flightTable = FlightTable(db)

  @Before
  fun testSetup() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
  }

  @Test
  fun getBasketsAvailableOnTest() = runTest {
    var availabilityBaskets =
        basketTable.getBasketsAvailableOn(
            flightTable = flightTable, dbs.date1, TimeSlot.AM, onError = { Assert.assertNull(it) })
    Assert.assertEquals(listOf(dbs.basket3), availabilityBaskets)

    availabilityBaskets =
        basketTable.getBasketsAvailableOn(
            flightTable = flightTable, dbs.date1, TimeSlot.PM, onError = { Assert.assertNull(it) })

    Assert.assertEquals(
        listOf(dbs.basket1, dbs.basket2).sortedBy { basket: Basket -> basket.id },
        availabilityBaskets.sortedBy { basket: Basket -> basket.id })

    availabilityBaskets =
        basketTable.getBasketsAvailableOn(
            flightTable = flightTable,
            dbs.dateNoFlight,
            TimeSlot.AM,
            onError = { Assert.assertNull(it) })

    Assert.assertEquals(
        listOf(dbs.basket1, dbs.basket2, dbs.basket3).sortedBy { basket: Basket -> basket.id },
        availabilityBaskets.sortedBy { basket: Basket -> basket.id })
  }
}
