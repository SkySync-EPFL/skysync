package ch.epfl.skysync.models.reports

import ch.epfl.skysync.models.user.User
import java.util.Date

interface Report {
  val id: String
  val author: String
  val begin: Date
  val end: Date
  val pauseDuration: Int // in milliseconds
  val comments: String

  /**
   * @param user the user to check if the report was authored by
   * @return true if this report was authored by the given user
   */
  fun authoredBy(user: User): Boolean {
    return author == user.id
  }
}
