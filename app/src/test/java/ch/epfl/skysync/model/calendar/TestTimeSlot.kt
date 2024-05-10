package ch.epfl.skysync.model.calendar

import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.calendar.getTimeSlot
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test

class TestTimeSlot {

  @Test
  fun `getTimeSlot returns AM if strictly between midnight and noon`() {
    val times =
        listOf(
            LocalTime.of(0, 0, 1),
            LocalTime.of(6, 23, 12),
            LocalTime.of(11, 59, 59),
        )
    for (time in times) {
      assertEquals(TimeSlot.AM, getTimeSlot(time))
    }
  }

  @Test
  fun `getTimeSlot returns PM if strictly after noon and before midnight`() {
    val times =
        listOf(
            LocalTime.of(12, 0, 1),
            LocalTime.of(23, 59, 59),
            LocalTime.of(17, 1, 12),
        )
    for (time in times) {
      assertEquals(TimeSlot.PM, getTimeSlot(time))
    }
  }

  @Test
  fun `getTimeSlot returns PM on boundary cases`() {
    val times =
        listOf(
            LocalTime.of(0, 0, 0),
            LocalTime.of(12, 0, 0),
        )
    for (time in times) {
      assertEquals(TimeSlot.PM, getTimeSlot(time))
    }
  }
}
