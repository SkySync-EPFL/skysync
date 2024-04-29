package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.Schema
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.location.Location
import ch.epfl.skysync.models.user.Admin
import com.google.android.gms.maps.model.LatLng

val UNSET_USER =
    Admin(
        id = UNSET_ID,
        firstname = "",
        lastname = "",
        availabilities = AvailabilityCalendar(),
        assignedFlights = FlightGroupCalendar()
    )

data class LocationSchema(
    val id: String? = null,
    val userId: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : Schema<Location> {
    override fun toModel(): Location {
        if (userId == null || latitude == null || longitude == null || id == null) {
            throw IllegalStateException("Missing fields for Location")
        }

        return Location(id, UNSET_USER.copy(id = userId!!), LatLng(latitude, longitude))
    }

    companion object {
        fun fromModel(location: Location): LocationSchema {
            return LocationSchema(
                id = location.id,
                userId = location.user.id,  // Assume User has an id field
                latitude = location.value.latitude,
                longitude = location.value.longitude
            )
        }
    }
}