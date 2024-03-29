package ch.epfl.skysync.model

import java.time.LocalDate

/** Represent the availability of a person for some period */
data class Availability(
    val id: String = UNSET_ID,
    val status: AvailabilityStatus,
    val timeSlot: TimeSlot,
    val date: LocalDate
)
