package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.Schema
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.RoleType
import com.google.firebase.firestore.DocumentId

data class FlightTypeSchema(
    @DocumentId val id: String? = null,
    val name: String? = null,
    val specialRoles: List<RoleType>? = null
) : Schema<FlightType> {
  override fun toModel(): FlightType {
    return FlightType(
        id = id!!,
        name = name!!,
        specialRoles = specialRoles!!,
    )
  }

  companion object {
    fun fromModel(model: FlightType): FlightTypeSchema {
      return FlightTypeSchema(id = model.id, name = model.name, specialRoles = model.specialRoles)
    }
  }
}
