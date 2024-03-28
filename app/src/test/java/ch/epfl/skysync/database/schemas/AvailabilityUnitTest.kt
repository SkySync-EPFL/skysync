package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.dataModels.calendarModels.Availability
import ch.epfl.skysync.dataModels.calendarModels.AvailabilityStatus
import ch.epfl.skysync.dataModels.calendarModels.TimeSlot
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date
import org.junit.Assert.*
import org.junit.Test

class AvailabilityUnitTest {

  /**
   * Test that both the casting from the schema format to the model format and from the model format
   * to the schema format work.
   */
  @Test
  fun modelSchemaCastingTest() {
    val personId = "personId"
    val localDate = LocalDate.now()
    val date = Date.from(localDate.atStartOfDay(ZoneOffset.UTC).toInstant())
    val availabilitySchema =
        AvailabilitySchema(
            id = "id",
            personId = personId,
            status = AvailabilityStatus.MAYBE,
            timeSlot = TimeSlot.PM,
            date = date)
    val availability =
        Availability(
            id = "id", status = AvailabilityStatus.MAYBE, timeSlot = TimeSlot.PM, date = localDate)

    assertEquals(availability, availabilitySchema.toModel())
    assertEquals(availabilitySchema, AvailabilitySchema.fromModel(personId, availability))
  }
}
