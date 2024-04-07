package ch.epfl.skysync.database.tables

import android.os.SystemClock
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.models.calendar.Availability
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.TimeSlot
import java.time.LocalDate
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
    table.deleteTable({})
    SystemClock.sleep(DB_SLEEP_TIME)
  }

  /**
   * Test the AvailabilityTable as one integration test as it is mostly a wrapper of the
   * FirestoreDatabase class (it has no relations) and the FirestoreDatabase has no mock class,
   * instead we use an emulator, meaning we can not test the `add` method in isolation for example.
   */
  @Test
  fun integrationTest() {
    val personId = "personId"
    val availability =
        Availability(
            status = AvailabilityStatus.MAYBE, timeSlot = TimeSlot.PM, date = LocalDate.now())

    // Step 1: Add

    var id = "__invalid_id__"
    var addComplete = false
    var addError = false

    // add an availability
    table.add(
        personId,
        availability,
        {
          id = it
          addComplete = true
        },
        { addError = true })

    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, addComplete)
    assertEquals(false, addError)

    // Step 2: Get

    var getAvailability: Availability? = null
    var getComplete = false
    var getError = false

    // retrieve the added availability
    table.get(
        id,
        {
          getAvailability = it
          getComplete = true
        },
        { getError = true })

    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, getComplete)
    assertEquals(false, getError)
    // the added then retrieved availability should be the same as the initial one
    assertEquals(availability.copy(id = id), getAvailability)

    // Step 3: Delete

    var deleteComplete = false
    var deleteError = false

    // delete the availability
    table.delete(id, { deleteComplete = true }, { deleteError = true })

    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, deleteComplete)
    assertEquals(false, deleteError)

    // Step 4: GetAll

    var getAllSize = -1
    var getAllComplete = false
    var getAllError = false

    // get all the availabilities
    table.getAll(
        {
          getAllSize = it.size
          getAllComplete = true
        },
        { getAllError = true })

    SystemClock.sleep(DB_SLEEP_TIME)

    assertEquals(true, getAllComplete)
    assertEquals(false, getAllError)
    // there should not be any availabilities left
    assertEquals(0, getAllSize)
  }
}
