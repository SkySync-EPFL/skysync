package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.FlightMemberSchema
import ch.epfl.skysync.database.schemas.FlightSchema
import ch.epfl.skysync.database.schemas.ReportSchema
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.reports.Report
import com.google.firebase.firestore.Filter
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class ReportTable(db:FirestoreDatabase): Table<Report, ReportSchema>(db, ReportSchema::class, PATH){


    /**
     * Add a new report to the database
     *
     * @param item The report to add to the database
     * @param onError Callback called when an error occurs
     */
    suspend fun add(item: Report, flightId: String, onError: ((Exception) -> Unit)? = null): String {
        return withErrorCallback(onError) { db.addItem(path, ReportSchema.fromModel(item, flightId)) }
    }
    suspend fun addAll(reports: List<Report>, flightId: String): Unit = coroutineScope {
         reports.map { report ->
                async {
                    add(report, flightId, onError = {throw it})
                }
            }
            .awaitAll()
    }

    suspend fun retrieveReports(flightId: String): List<Report> = coroutineScope {
       query(Filter.equalTo("flightId", flightId))
    }


companion object {
    const val PATH = "report"
}
}