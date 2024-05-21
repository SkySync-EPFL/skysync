package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.FlightSchema
import ch.epfl.skysync.database.schemas.ReportSchema
import ch.epfl.skysync.models.reports.Report
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
    suspend fun add(item: Report, onError: ((Exception) -> Unit)? = null): String {
        return withErrorCallback(onError) { db.addItem(path, ReportSchema.fromModel(item)) }
    }

    suspend fun retrieveReports(schema: FlightSchema): List<Report> = coroutineScope {
        schema.reportIds!!
            .map { rid ->
                async {
                    val report: Report? = get(rid, onError = {throw it})
                    report
                }
            }
            .awaitAll()
            .filterNotNull()
    }


companion object {
    const val PATH = "report"
}
}