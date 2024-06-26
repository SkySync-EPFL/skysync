package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.DateUtility
import ch.epfl.skysync.database.Schema
import ch.epfl.skysync.models.calendar.Availability
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.TimeSlot
import com.google.firebase.firestore.DocumentId
import java.util.Date

data class AvailabilitySchema(
    @DocumentId val id: String? = null,
    val userId: String? = null,
    val status: AvailabilityStatus? = null,
    val timeSlot: TimeSlot? = null,
    /** We use the Date class instead of the LocalDate for Firestore see [DateUtility] */
    val date: Date? = null
) : Schema<Availability> {
  override fun toModel(): Availability {
    return Availability(
        id!!,
        status!!,
        timeSlot!!,
        DateUtility.dateToLocalDate(date!!),
    )
  }

  companion object {
    fun fromModel(userId: String, model: Availability): AvailabilitySchema {
      return AvailabilitySchema(
          model.id,
          userId,
          model.status,
          model.timeSlot,
          DateUtility.localDateToDate(model.date),
      )
    }
  }
}
