package ch.epfl.skysync.models.reports

import java.util.Date

interface Report {
  val id: String
  val author: String
  val begin: Date
  val end: Date
  val pauseDuration: Int // in milliseconds
  val comments: String
}
