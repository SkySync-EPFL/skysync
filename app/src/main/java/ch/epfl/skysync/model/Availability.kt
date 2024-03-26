package ch.epfl.skysync.model

import java.util.Date

/** Represent the availability of a person for some period */
data class Availability(
    val id: String = UNSET_ID,
    val personId: String,
    val from: Date,
    val to: Date
)
