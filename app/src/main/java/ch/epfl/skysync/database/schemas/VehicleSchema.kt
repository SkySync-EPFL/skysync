package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.Schema
import ch.epfl.skysync.models.flight.Vehicle
import com.google.firebase.firestore.DocumentId

data class VehicleSchema(
    @DocumentId val id: String? = null,
    val name: String? = null,
) : Schema<Vehicle> {
  override fun toModel(): Vehicle {
    return Vehicle(
        id = id!!,
        name = name!!,
    )
  }

  companion object {
    fun fromModel(model: Vehicle): VehicleSchema {
      return VehicleSchema(
          id = model.id,
          name = model.name,
      )
    }
  }
}
