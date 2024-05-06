package ch.epfl.skysync.models.reports

import ch.epfl.skysync.models.flight.Vehicle
import java.util.Date

interface Report {
  val begin: Date
  val end: Date
  val pauseDuration: Long? // in milliseconds
  val comments: String
  val vehicleProblems: Map<Vehicle, String>
}
