package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.Schema
import ch.epfl.skysync.model.Availability
import ch.epfl.skysync.model.AvailabilityStatus
import com.google.firebase.firestore.DocumentId
import java.util.Date

data class AvailabilitySchema(
    @DocumentId val id: String? = null,
    val personId: String? = null,
    val status: AvailabilityStatus? = null,
    val from: Date? = null,
    val to: Date? = null
) : Schema<Availability> {
  override fun toModel(): Availability {
    return Availability(
        id!!,
        status!!,
        from!!,
        to!!,
    )
  }

  companion object {
    fun fromModel(personId: String, model: Availability): AvailabilitySchema {
      return AvailabilitySchema(
          model.id,
          personId,
          model.status,
          model.from,
          model.to,
      )
    }
  }
}
