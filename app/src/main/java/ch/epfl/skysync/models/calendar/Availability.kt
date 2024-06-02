package ch.epfl.skysync.models.calendar

import ch.epfl.skysync.models.UNSET_ID
import java.time.LocalDate

/**
 * Represents the availability of a person for a certain period.
 *
 * This is an immutable data class that holds the availability status, time slot, and date.
 *
 * @property id The unique identifier for this availability. Defaults to [UNSET_ID].
 * @property status The status of the availability.
 * @property timeSlot The time slot for which this availability applies.
 * @property date The date for which this availability applies.
 */
data class Availability(
    val id: String = UNSET_ID,
    val status: AvailabilityStatus,
    override val timeSlot: TimeSlot,
    override val date: LocalDate
) : CalendarViewable
