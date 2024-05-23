package ch.epfl.skysync.screens.reports

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import ch.epfl.skysync.database.DateUtility
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.flight.FlightColor
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.location.LocationPoint
import ch.epfl.skysync.models.reports.PilotReport
import ch.epfl.skysync.models.user.Pilot
import java.time.Instant
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ReportDetailsTest {
  @get:Rule val composeTestRule = createComposeRule()
  private val calendar = Calendar.getInstance()
  private val pilot =
      Pilot("12345", "Ryan", "Mehenni", "@gmail", qualification = BalloonQualification.LARGE)
  val report =
      PilotReport(
          id = "1234",
          author = pilot.id,
          effectivePax = 2,
          takeOffTime = DateUtility.localDateToDate(LocalDate.of(2024, 5, 24)),
          landingTime = DateUtility.localDateToDate(LocalDate.of(2024, 2, 22)),
          takeOffLocation = LocationPoint(21, 46.0, 6.0, "Lignon"),
          landingLocation = LocationPoint(21, 46.2, 6.1, "Libelules"),
          begin = DateUtility.localDateToDate(LocalDate.of(2024, 1, 11)),
          end = DateUtility.localDateToDate(LocalDate.of(2024, 11, 26)),
          pauseDuration = 0,
          comments = "Some comments that is interesting",
      )
  val flight =
      FinishedFlight(
          "1234",
          3,
          Team(listOf(Role(RoleType.CREW), Role(RoleType.CREW))),
          FlightType.DISCOVERY,
          Balloon("Balloon Name", BalloonQualification.LARGE, "Ballon Name"),
          Basket("Basket Name", true, "1234"),
          LocalDate.now().plusDays(3),
          TimeSlot.PM,
          listOf(
              Vehicle("Peugeot 308", "1234"),
          ),
          color = FlightColor.RED,
          takeOffTime = DateUtility.localDateToDate(LocalDate.of(2024, 5, 19)),
          takeOffLocation = LocationPoint(21, 46.0, 6.0, "Verier"),
          landingTime = Date.from(Instant.now()),
          landingLocation = LocationPoint(21, 46.2, 6.1, "Vernier"),
          flightTime = 20000,
          reportId = listOf(report))

  private fun helper(field: String, value: String) {
    composeTestRule.onNodeWithText(field).assertIsDisplayed()
    composeTestRule.onNodeWithText(value).assertIsDisplayed()
  }

  @Before
  fun setUp() = runTest {
    composeTestRule.setContent {
      ReportDetail(listOf(pilot), listOf(report), false, pilot.id, flight.id)
    }
  }

  @Test
  fun showPilotReportStats() {
    helper("Comments", report.comments)
    helper("Begin", DateUtility.localDateToString(DateUtility.dateToLocalDate(report.begin)))
    helper("End", DateUtility.localDateToString(DateUtility.dateToLocalDate(report.end)))
    helper("Number of passengers on board", report.effectivePax.toString())
    helper("Takeoff location", report.takeOffLocation.name)
    helper(
        "Takeoff time",
        DateUtility.localDateToString(DateUtility.dateToLocalDate(report.takeOffTime)))
    helper("Landing location", report.landingLocation.name)
    helper(
        "Landing time",
        DateUtility.localDateToString(DateUtility.dateToLocalDate(report.landingTime)))
  }
}
