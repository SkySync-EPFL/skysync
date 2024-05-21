package ch.epfl.skysync.models.reports

import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.user.User
import java.util.Date

interface Report {
  val id: String
  val author: String
  val begin: Date
  val end: Date
  val pauseDuration: Long? // in milliseconds
  val comments: String
  val vehicleProblems: Map<Vehicle, String>
}
