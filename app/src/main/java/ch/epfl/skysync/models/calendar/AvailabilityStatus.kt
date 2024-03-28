package ch.epfl.skysync.models.calendar

/**
 * Represent three statuses: "OK" for available, "MAYBE" for pending confirmation, and "NO" for not
 * available
 */
enum class AvailabilityStatus {
  OK,
  MAYBE,
  NO
}
