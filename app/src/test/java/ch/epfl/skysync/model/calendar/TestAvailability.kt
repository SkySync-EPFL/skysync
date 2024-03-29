package ch.epfl.skysync.model.calendar

import ch.epfl.skysync.models.calendar.Availability
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.TimeSlot
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TestAvailability {

  lateinit var initAvailability: Availability

  @Before
  fun setUp() {
    initAvailability = Availability(
      status=AvailabilityStatus.MAYBE,
      timeSlot = TimeSlot.AM,
      date = LocalDate.of(2024, 4, 1)
    )
  }
  @Test
  fun `setStatus changes status immutably`() {
    val oldStatus = initAvailability.status
    val newStatus = AvailabilityStatus.OK
    val newAvailability = initAvailability.setStatus(newStatus)
    assertEquals(newAvailability.status, newStatus)
    // check that old object remains unchanged
    assertEquals(initAvailability.status, oldStatus)
  }

  @Test
  fun `setStatus for same status is secure`() {
    val oldStatus = initAvailability.status
    val newAvailability = initAvailability.setStatus(oldStatus)
    assertEquals(newAvailability.status, oldStatus)
  }







}
