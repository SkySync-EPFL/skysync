package ch.epfl.skysync.tables

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.DateUtility.localDateAndTimeToDate
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.tables.FlightTable
import ch.epfl.skysync.database.tables.ReportTable
import ch.epfl.skysync.models.reports.FlightReport
import java.time.LocalDate
import java.time.LocalTime
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReportTableTest {

  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  private val reportTable = ReportTable(db)
  private val flightTable = FlightTable(db)

  @Before
  fun testSetup() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
  }

  @Test
  fun addAndRetrieveOneReport() = runTest {
    val start = localDateAndTimeToDate(LocalDate.of(2022, 1, 1), LocalTime.of(12, 0))
    val stop = localDateAndTimeToDate(LocalDate.of(2022, 1, 1), LocalTime.of(13, 1))
    val flightId = dbs.flight1.id
    var report =
        FlightReport(
            author = dbs.admin2.id, begin = start, end = stop, pauseDuration = 0, comments = "test")
    val reportId = reportTable.add(report, flightId)
    report = report.copy(id = reportId)
    val retrievedReportsForFlight = reportTable.retrieveReports(flightId)
    assertEquals(1, retrievedReportsForFlight.size)
    assertEquals(report, retrievedReportsForFlight[0])
  }
}
