package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.Schema
import ch.epfl.skysync.models.reports.FlightReport
import ch.epfl.skysync.models.reports.Report
import com.google.firebase.firestore.DocumentId
import java.util.Date

data class ReportSchema(
    @DocumentId val id: String? = null,
    val authorId: String? = null,
    val flightId: String? = null,
    val shiftBegin: Date? = null,
    val shiftEnd: Date? = null,
    val pauseDuration: Int? = null, // in milliseconds
    val comment: String? = null,
) : Schema<Report> {
  override fun toModel(): Report {
    return FlightReport(
        id = id!!,
        author = authorId!!,
        begin = shiftBegin!!,
        end = shiftEnd!!,
        pauseDuration = pauseDuration!!,
        comments = comment!!,
    )
  }

  companion object {
    fun fromModel(model: Report, flightId: String?): ReportSchema {
      return ReportSchema(
          id = model.id,
          authorId = model.author,
          flightId = flightId,
          shiftBegin = model.begin,
          shiftEnd = model.end,
          pauseDuration = model.pauseDuration,
          comment = model.comments,
      )
    }
  }
}
