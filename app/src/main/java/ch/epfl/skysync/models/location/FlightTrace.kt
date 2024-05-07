package ch.epfl.skysync.models.location

import ch.epfl.skysync.models.UNSET_ID

data class FlightTrace(
    val id: String = UNSET_ID,
    val trace: List<LocationPoint>,
)
