package ch.epfl.skysync.models.reports

import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.user.User
import java.time.LocalTime
import java.util.Date

interface Report {
  val id: String
  val author: User
  val begin: LocalTime
  val end: LocalTime
  val pauseDuration: Long? // in milliseconds
  val comments: String
  val vehicleProblems: Map<Vehicle, String>
}
