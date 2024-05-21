package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.Schema
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.reports.FlightReport
import ch.epfl.skysync.models.reports.Report
import ch.epfl.skysync.models.user.User
import com.google.firebase.firestore.DocumentId
import java.util.Date

data class ReportSchema(
    @DocumentId val id: String? = null,
    val authorId: String? = null,
    val shiftBegin: Date? = null,
    val shiftEnd: Date? = null,
    val pauseDuration: Long?, // in milliseconds
    val comment: String? = null,
    val vehicleProblems: Map<String, String>
) : Schema<Report> {
  override fun toModel(): Report {
      throw UnsupportedOperationException()
  }

  companion object {
    fun fromModel(model: Report): ReportSchema {
      return ReportSchema(
          id = model.id,
            authorId = model.author,
            shiftBegin = model.begin,
            shiftEnd = model.end,
            pauseDuration = model.pauseDuration,
            comment = model.comments,
            vehicleProblems = model.vehicleProblems.map {
                it.key.id to it.value
            }.toMap()
      )
    }
  }
}
