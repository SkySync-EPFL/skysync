package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.Schema
import ch.epfl.skysync.models.location.Location
import ch.epfl.skysync.models.location.LocationPoint
import com.google.firebase.firestore.DocumentId

data class LocationSchema(
    @DocumentId val id: String? = null,
    val userId: String? = null,
    val time: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : Schema<Location> {
  override fun toModel(): Location {
    return Location(
        id!!,
        userId!!,
        LocationPoint(time!!, latitude!!, longitude!!),
    )
  }

  companion object {
    fun fromModel(model: Location): LocationSchema {
      return LocationSchema(
          id = model.id,
          userId = model.userId,
          time = model.point.time,
          latitude = model.point.latitude,
          longitude = model.point.longitude)
    }
  }
}
