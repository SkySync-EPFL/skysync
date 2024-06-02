package ch.epfl.skysync.database

/**
 * Enum class representing the status of a flight in the database.
 *
 * The status can be one of the following:
 * - PLANNED: The flight is planned but not yet confirmed.
 * - CONFIRMED: The flight is confirmed and scheduled.
 * - FINISHED: The flight has been completed.
 */
enum class DbFlightStatus {
  PLANNED,
  CONFIRMED,
  FINISHED
}
