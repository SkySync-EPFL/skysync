package ch.epfl.skysync.database.tables

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.models.calendar.Availability
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.TimeSlot
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// sleep to let the db requests the time to be done
const val DB_SLEEP_TIME = 3000L

@RunWith(AndroidJUnit4::class)
class AvailabilityTableUnitTest {
  private val table = AvailabilityTable(FirestoreDatabase(useEmulator = true))

  @Before
  fun testSetup() {
    runTest { table.deleteTable() }
  }

  /**
   * Test the AvailabilityTable as one integration test as it is mostly a wrapper of the
   * FirestoreDatabase class (it has no relations) and the FirestoreDatabase has no mock class,
   * instead we use an emulator, meaning we can not test the `add` method in isolation for example.
   */
  @Test
  fun integrationTest() {
    runTest {
      val userId = "userId"
      val availability =
          Availability(
              status = AvailabilityStatus.MAYBE, timeSlot = TimeSlot.PM, date = LocalDate.now())

      // add an availability
      val id = table.add(userId, availability)

      // retrieve the added availability
      var getAvailability = table.get(id)

      // the added then retrieved availability should be the same as the initial one
      assertEquals(availability.copy(id = id), getAvailability)

      val updateAvailability =
          Availability(
              id = id,
              status = AvailabilityStatus.OK,
              timeSlot = TimeSlot.PM,
              date = LocalDate.now())
      table.update(userId, id, updateAvailability)

      getAvailability = table.get(id)

      // the updated availability should be the same as the initial one
      assertEquals(updateAvailability, getAvailability)

      // delete the availability
      table.delete(id)

      // get all the availabilities
      val availabilities = table.getAll()

      // there should not be any availabilities left
      assertEquals(0, availabilities.size)
    }
  }
}
