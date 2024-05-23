package ch.epfl.skysync.model.flight

import ch.epfl.skysync.database.DateUtility
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.flight.FlightStatus
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.location.FlightTrace
import ch.epfl.skysync.models.location.LocationPoint
import ch.epfl.skysync.models.reports.FlightReport
import ch.epfl.skysync.models.user.Admin
import ch.epfl.skysync.models.user.Crew
import ch.epfl.skysync.models.user.Pilot
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
val crew1 = Crew("1", "John", "Doe", "")
val admin1 = Admin("3", "Betina", "Doe", "")
val pilot1 = Pilot("2", "Jane", "Doe", "", qualification = BalloonQualification.LARGE)

class TestFlightStatus {
  val plannedFlight =
      PlannedFlight(
          nPassengers = 2,
          team = Team(roles = listOf(Role(RoleType.PILOT, pilot1), Role(RoleType.CREW, crew1))),
          flightType = FlightType.DISCOVERY,
          balloon = Balloon("1", BalloonQualification.LARGE),
          basket = Basket("1", true),
          date = LocalDate.of(2000, 1, 1),
          timeSlot = TimeSlot.AM,
          vehicles = emptyList(),
          id = "1")
  val finishedFlight =
      FinishedFlight(
          nPassengers = 2,
          team = Team(roles = listOf(Role(RoleType.PILOT, pilot1), Role(RoleType.CREW, crew1))),
          flightType = FlightType.DISCOVERY,
          balloon = Balloon("1", BalloonQualification.LARGE),
          basket = Basket("1", true),
          date = LocalDate.of(2000, 1, 1),
          timeSlot = TimeSlot.AM,
          vehicles = emptyList(),
          id = "1",
          takeOffTime = DateUtility.localDateToDate(LocalDate.of(2000, 1, 1)),
          takeOffLocation = LocationPoint(1, 0.0, 0.0),
          landingTime = DateUtility.localDateToDate(LocalDate.of(2000, 1, 1)),
          landingLocation = LocationPoint(1, 0.0, 0.0),
          flightTime = 1000,
          reportId = emptyList(),
          flightTrace = FlightTrace(UNSET_ID, emptyList()),
          thisFlightStatus = FlightStatus.MISSING_REPORT)

  val report1 =
      FlightReport(
          author = crew1.id,
          begin =
              DateUtility.localDateAndTimeToDate(
                  LocalDate.of(2022, 12, 11),
                  LocalTime.of(23, 0, 1),
              ),
          end =
              DateUtility.localDateAndTimeToDate(
                  LocalDate.of(2022, 12, 11),
                  LocalTime.of(14, 1, 0),
              ),
          pauseDuration = 101,
          comments = "hola test",
      )

  @Test
  fun `no filtering is done if no confirmed present`() {
    val noConfirmed = listOf(finishedFlight, plannedFlight)
    var filtered = FlightStatus.filterCompletedFlights(noConfirmed, crew1)
    assertEquals(filtered, noConfirmed)
  }

  @Test
  fun `filtering is done if confirmed present for admin`() {
    val confirmedFlight = finishedFlight.copy(team = Team(roles = listOf()))
    var filtered =
        FlightStatus.filterCompletedFlights(listOf(confirmedFlight, plannedFlight), admin1)
    assertEquals(listOf(plannedFlight), filtered)
  }

  @Test
  fun `filtering is done if confirmed present for crew`() {
    val confirmedFlight =
        finishedFlight.copy(
            team = Team(roles = listOf(Role(RoleType.CREW, crew1))), reportId = listOf(report1))
    var filtered =
        FlightStatus.filterCompletedFlights(listOf(confirmedFlight, plannedFlight), crew1)
    assertEquals(listOf(plannedFlight), filtered)
  }
}
