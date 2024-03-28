package ch.epfl.skysync.models.calendar

import ch.epfl.skysync.models.user.UNSET_ID
import java.time.LocalDate

/** Represent the availability of a person for some period */
data class Availability(
    val id: String = UNSET_ID,
    val status: AvailabilityStatus,
    override val timeSlot: TimeSlot,
    override val date: LocalDate
): CalendarViewable {
    fun setStatus(status: AvailabilityStatus): Availability {
        if (status == this.status) return this
        return Availability(id, status, timeSlot, date)
    }
}