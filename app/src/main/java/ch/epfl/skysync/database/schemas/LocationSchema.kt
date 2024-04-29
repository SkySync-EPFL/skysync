package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.Schema
import ch.epfl.skysync.models.location.Location

data class LocationSchema(
    val id: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : Schema<Location> {
    override fun toModel(): Location {
        throw NotImplementedError()
    }

    companion object {
        fun fromModel(location: Location): LocationSchema {
            return LocationSchema(
                id = location.id,
                latitude = location.value.latitude,
                longitude = location.value.longitude
            )
        }
    }
}