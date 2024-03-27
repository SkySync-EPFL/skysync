package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.Schema
import ch.epfl.skysync.model.Availability
import ch.epfl.skysync.model.AvailabilityStatus
import ch.epfl.skysync.model.TimeSlot
import com.google.firebase.firestore.DocumentId
import java.time.ZoneOffset
import java.util.Date

data class AvailabilitySchema(
    @DocumentId val id: String? = null,
    val personId: String? = null,
    val status: AvailabilityStatus? = null,
    val timeSlot: TimeSlot? = null,
    // We use the Date class instead of the LocalDate one because
    // firestore store LocalDate as a collection of fields
    // whereas it stores Date as a string, which is simpler
    // and will lead to easier queries
    val date: Date? = null
) : Schema<Availability> {
    override fun toModel(): Availability {
        return Availability(
            id!!,
            status!!,
            timeSlot!!,
            (date!!).toInstant().atZone(ZoneOffset.systemDefault()).toLocalDate(),
        )
    }

    companion object {
        fun fromModel(personId: String, model: Availability): AvailabilitySchema {
            return AvailabilitySchema(
                model.id,
                personId,
                model.status,
                model.timeSlot,
                Date.from(model.date.atStartOfDay(ZoneOffset.UTC).toInstant()),
            )
        }
    }
}
