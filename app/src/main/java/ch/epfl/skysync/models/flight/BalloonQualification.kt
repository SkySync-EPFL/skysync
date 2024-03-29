package ch.epfl.skysync.models.flight

/**
 * defines the different balloon sizes/qualifications with the natural ordering small < medium <
 * large
 */
enum class BalloonQualification {
  SMALL,
  MEDIUM,
  LARGE;

  /** returns true if this qualification >= givenQualification */
  fun greaterEqual(qualification: BalloonQualification): Boolean {
    return this.ordinal >= qualification.ordinal
  }
}
