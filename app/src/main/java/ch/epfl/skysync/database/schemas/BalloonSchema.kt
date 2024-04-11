package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.Schema
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import com.google.firebase.firestore.DocumentId

data class BalloonSchema(
    @DocumentId val id: String? = null,
    val name: String? = null,
    val qualification: BalloonQualification? = null,
) : Schema<Balloon> {
  override fun toModel(): Balloon {
    return Balloon(
        id = id!!,
        name = name!!,
        qualification = qualification!!,
    )
  }

  companion object {
    fun fromModel(model: Balloon): BalloonSchema {
      return BalloonSchema(
          id = model.id,
          name = model.name,
          qualification = model.qualification,
      )
    }
  }
}
