package ch.epfl.skysync.models.flight

/**
 * Enum class representing the different balloon sizes/qualifications.
 *
 * The sizes/qualifications are ordered as follows:
 * - SMALL
 * - MEDIUM
 * - LARGE
 *
 * The [greaterEqual] function is used to compare this qualification with a given qualification.
 */
enum class BalloonQualification {
  SMALL,
  MEDIUM,
  LARGE;

  /**
   * Checks if this qualification is greater than or equal to the given qualification.
   *
   * @param qualification The qualification to compare with.
   * @return True if this qualification is greater than or equal to the given qualification, false
   *   otherwise.
   */
  fun greaterEqual(qualification: BalloonQualification): Boolean {
    return this.ordinal >= qualification.ordinal
  }
}
