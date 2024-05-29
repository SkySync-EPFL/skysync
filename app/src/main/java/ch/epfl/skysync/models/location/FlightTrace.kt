package ch.epfl.skysync.models.location

import ch.epfl.skysync.models.UNSET_ID
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class FlightTrace(
    val id: String = UNSET_ID,
    val trace: List<LocationPoint>,
)



