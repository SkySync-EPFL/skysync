package ch.epfl.skysync.models.calendar

/**
 * Enum class representing the availability status of a person.
 *
 * The status can be one of the following:
 * - OK: The person is available.
 * - MAYBE: The person's availability is pending confirmation.
 * - NO: The person is not available.
 * - UNDEFINED: The availability status is not defined.
 * - ASSIGNED: The person is assigned to a flight.
 *
 * The [next] function is used to cycle through the statuses in a round-robin fashion.
 */
enum class AvailabilityStatus {
  OK,
  MAYBE,
  NO,
  UNDEFINED,
  ASSIGNED;

  /**
   * Returns the next availability status in a round-robin fashion.
   *
   * The order of the statuses is as follows: UNDEFINED -> OK -> MAYBE -> NO -> UNDEFINED The
   * ASSIGNED status is a terminal state and does not change.
   *
   * @return The next availability status.
   */
  fun next(): AvailabilityStatus {
    return when (this) {
      UNDEFINED -> OK
      OK -> MAYBE
      MAYBE -> NO
      NO -> UNDEFINED
      ASSIGNED -> ASSIGNED
    }
  }
}
