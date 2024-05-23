package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.ReportSchema
import ch.epfl.skysync.models.reports.Report
import com.google.firebase.firestore.Filter
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class ReportTable(db: FirestoreDatabase) :
    Table<Report, ReportSchema>(db, ReportSchema::class, PATH) {

  /**
   * Add a new report to the database
   *
   * @param item The report to add to the database
   * @param onError Callback called when an error occurs
   */
  suspend fun add(item: Report, flightId: String, onError: ((Exception) -> Unit)? = null): String {
    return withErrorCallback(onError) { db.addItem(path, ReportSchema.fromModel(item, flightId)) }
  }

  /**
   * Add a list of reports belonging to a given flight to the database
   *
   * @param reports The list of reports to add to the database
   * @param flightId The id of the flight to which the reports belong
   */
  suspend fun addAll(reports: List<Report>, flightId: String): Unit = coroutineScope {
    reports.map { report -> async { add(report, flightId, onError = { throw it }) } }.awaitAll()
  }

  /**
   * Retrieve all reports for a given flight
   *
   * @param flightId The id of the flight for which to retrieve the reports
   * @return The list of reports for the given flight
   */
  suspend fun retrieveReports(
      flightId: String,
      onError: ((Exception) -> Unit)? = null
  ): List<Report> = coroutineScope {
    query(Filter.equalTo("flightId", flightId), onError = onError)
  }

  companion object {
    const val PATH = "report"
  }
}
