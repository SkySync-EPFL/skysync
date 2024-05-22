package ch.epfl.skysync.database.tables

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.DateUtility
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.location.FlightTrace
import ch.epfl.skysync.models.location.LocationPoint
import ch.epfl.skysync.models.reports.FlightReport
import java.time.LocalTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class FlightTableUnitTest {
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  private val flightTable = FlightTable(db)
    private val reportTable = ReportTable(db)
  private val flightMemberTable = FlightMemberTable(db)

  @Before
  fun testSetup() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
  }

  @Test
  fun addAndRetrieveFinishFlight() = runTest {
    var finishedFlight =
        dbs.flight4.finishFlight(
            takeOffTime = LocalTime.of(12, 0, 0),
            landingTime = LocalTime.of(14, 0, 0),
            takeOffLocation = LocationPoint(0, 0.1, 0.1, "TakeOffSpot"),
            landingLocation = LocationPoint(0, 0.1, 0.1, "LandingSpot"),
            flightTime = 2,
            flightTrace = FlightTrace(dbs.flight4.id, listOf()))
      var report1 = FlightReport(
          author = "ME",
          begin = DateUtility.localDateAndTimeToDate(
              LocalDate.of(2021, 1, 1),
              LocalTime.of(12, 0, 0),
          ),
          end = DateUtility.localDateAndTimeToDate(
              LocalDate.of(2021, 1, 1),
              LocalTime.of(12, 2, 0),
          ),
          pauseDuration = 10,
          comments = "hola",
      )
      finishedFlight = finishedFlight.copy(reportId = listOf(report1))
    flightTable.update(dbs.flight4.id, finishedFlight, onError = { assertNull(it) })
      val foundReports = reportTable.retrieveReports(dbs.flight4.id)
        assertEquals(1, foundReports.size)
      val reportId = foundReports[0].id
      report1 = report1.copy(id = reportId)
      finishedFlight = finishedFlight.copy(reportId = listOf(report1))
    val retrievedFlight = flightTable.get(dbs.flight4.id, onError = { assertNull(it) })
    assertEquals(finishedFlight, retrievedFlight)
  }

  @Test
  fun getTest() = runTest {
    val flight = flightTable.get(dbs.flight1.id, onError = { assertNull(it) })
    assertEquals(dbs.flight1, flight)

    val confirmedFlight = flightTable.get(dbs.flight4.id, onError = { assertNull(it) })!!
    assertEquals(dbs.flight4, confirmedFlight)
  }

  @Test
  fun updateTest() = runTest {
    val newTeam = Team(roles = listOf(Role(RoleType.PILOT, dbs.pilot2)))
    val updateFlight1 = dbs.flight1.copy(nPassengers = dbs.flight1.nPassengers + 1, team = newTeam)

    flightTable.update(dbs.flight1.id, updateFlight1, onError = { assertNull(it) })

    val flight = flightTable.get(dbs.flight1.id, onError = { assertNull(it) })

    assertEquals(updateFlight1, flight)

    val flightMembers =
        flightMemberTable.getAll(onError = { assertNull(it) }).filter { flightMemberSchema ->
          flightMemberSchema.flightId == dbs.flight1.id
        }
    assertEquals(1, flightMembers.size)
    assertEquals(dbs.pilot2.id, flightMembers.find { it.flightId == dbs.flight1.id }?.userId)
  }

  @Test
  fun deleteTest() = runTest {
    flightTable.delete(dbs.flight1.id, onError = { assertNull(it) })

    val flightMembers = flightMemberTable.getAll(onError = { assertNull(it) })

    val flights = flightTable.getAll(onError = { assertNull(it) })

    assertEquals(7, flightMembers.size)
    assertEquals(
        listOf(dbs.flight2, dbs.flight3, dbs.flight4).sortedBy { f -> f.id },
        flights.sortedBy { f -> f.id })
  }
}
