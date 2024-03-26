package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.SchemaDecode
import ch.epfl.skysync.database.SchemaEncode
import ch.epfl.skysync.model.Availability
import com.google.firebase.firestore.DocumentId
import java.util.Date

data class AvailabilitySchema(
    @DocumentId val id: String? = null,
    val personId: String? = null,
    val from: Date? = null,
    val to: Date? = null
) : SchemaDecode<Availability> {
  override fun toModel(): Availability {
    return Availability(
        id!!,
        personId!!,
        from!!,
        to!!,
    )
  }

  companion object : SchemaEncode<Availability, AvailabilitySchema> {
    override fun fromModel(model: Availability): AvailabilitySchema {
      return AvailabilitySchema(
          model.id,
          model.personId,
          model.from,
          model.to,
      )
    }
  }
}
