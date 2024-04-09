package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.DateLocalDateConverter
import ch.epfl.skysync.database.Schema
import ch.epfl.skysync.models.calendar.Availability
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.TimeSlot
import com.google.firebase.firestore.DocumentId
import java.util.Date

data class AvailabilitySchema(
    @DocumentId val id: String? = null,
    val personId: String? = null,
    val status: AvailabilityStatus? = null,
    val timeSlot: TimeSlot? = null,
    /** We use the Date class instead of the LocalDate for Firestore see [DateLocalDateConverter] */
    val date: Date? = null
) : Schema<Availability> {
  override fun toModel(): Availability {
    return Availability(
        id!!,
        status!!,
        timeSlot!!,
        DateLocalDateConverter.dateToLocalDate(date!!),
    )
  }

  companion object {
    fun fromModel(personId: String, model: Availability): AvailabilitySchema {
      return AvailabilitySchema(
          model.id,
          personId,
          model.status,
          model.timeSlot,
          DateLocalDateConverter.localDateToDate(model.date),
      )
    }
  }
}
