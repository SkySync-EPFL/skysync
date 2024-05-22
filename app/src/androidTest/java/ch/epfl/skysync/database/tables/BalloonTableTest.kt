package ch.epfl.skysync.tables

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.tables.BalloonTable
import ch.epfl.skysync.database.tables.FlightTable
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BalloonTableTest {

  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  private val balloonTable = BalloonTable(db)
  private val flightTable = FlightTable(db)

  @Before
  fun testSetup() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
  }

  @Test
  fun getBalloonsAvailableOnTest() = runTest {
    var availabilityBalloons =
        balloonTable.getBalloonsAvailableOn(
            flightTable = flightTable, dbs.date1, TimeSlot.AM, onError = { Assert.assertNull(it) })

    Assert.assertEquals(listOf(dbs.balloon3), availabilityBalloons)

    availabilityBalloons =
        balloonTable.getBalloonsAvailableOn(
            flightTable = flightTable, dbs.date1, TimeSlot.PM, onError = { Assert.assertNull(it) })

    Assert.assertEquals(
        listOf(dbs.balloon1, dbs.balloon2).sortedBy { balloon: Balloon -> balloon.id },
        availabilityBalloons.sortedBy { balloon: Balloon -> balloon.id })

    availabilityBalloons =
        balloonTable.getBalloonsAvailableOn(
            flightTable = flightTable,
            dbs.dateNoFlight,
            TimeSlot.AM,
            onError = { Assert.assertNull(it) })

    Assert.assertEquals(
        listOf(dbs.balloon1, dbs.balloon2, dbs.balloon3).sortedBy { balloon: Balloon ->
          balloon.id
        },
        availabilityBalloons.sortedBy { balloon: Balloon -> balloon.id })
  }
}
