package ch.epfl.skysync.tables

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.tables.FlightTable
import ch.epfl.skysync.database.tables.VehicleTable
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Vehicle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VehicleTableTest {

  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  private val vehicleTable = VehicleTable(db)
  private val flightTable = FlightTable(db)

  @Before
  fun testSetup() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
  }

  @Test
  fun getVehiclesAvailableOnTest() = runTest {
    var availabilityVehicles =
        vehicleTable.getVehiclesAvailableOn(
            flightTable = flightTable, dbs.date1, TimeSlot.AM, onError = { Assert.assertNull(it) })
    Assert.assertEquals(listOf<Vehicle>(), availabilityVehicles)

    availabilityVehicles =
        vehicleTable.getVehiclesAvailableOn(
            flightTable = flightTable, dbs.date1, TimeSlot.PM, onError = { Assert.assertNull(it) })

    Assert.assertEquals(listOf(dbs.vehicle3), availabilityVehicles)

    availabilityVehicles =
        vehicleTable.getVehiclesAvailableOn(
            flightTable = flightTable,
            dbs.dateNoFlight,
            TimeSlot.AM,
            onError = { Assert.assertNull(it) })

    Assert.assertEquals(
        listOf(dbs.vehicle1, dbs.vehicle2, dbs.vehicle3).sortedBy { vehicle: Vehicle ->
          vehicle.id
        },
        availabilityVehicles.sortedBy { vehicle: Vehicle -> vehicle.id })
  }
}
