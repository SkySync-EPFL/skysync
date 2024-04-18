package ch.epfl.skysync.models.calendar

import ch.epfl.skysync.models.UNSET_ID
import java.time.LocalDate

/** Represent the availability of a person for some period (immutable class) */
data class Availability(
    var id: String = UNSET_ID,
    val status: AvailabilityStatus,
    override val timeSlot: TimeSlot,
    override val date: LocalDate
) : CalendarViewable
