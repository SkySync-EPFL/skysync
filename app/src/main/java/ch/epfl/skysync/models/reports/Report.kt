package ch.epfl.skysync.models.reports

import ch.epfl.skysync.models.flight.Vehicle
import java.sql.Time

interface Report {
  val begin: Time
  val end: Time
  val pause: Boolean
  val pauseDuration: Long
  val comments: String
  val vehicleProblems: Map<Vehicle, String>
}
