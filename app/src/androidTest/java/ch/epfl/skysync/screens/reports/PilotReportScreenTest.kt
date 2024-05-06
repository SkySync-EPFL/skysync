package ch.epfl.skysync.screens.reports

import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.location.Location
import ch.epfl.skysync.models.reports.PilotReport
import ch.epfl.skysync.models.user.Pilot
import com.google.android.gms.maps.model.LatLng
import java.sql.Time
import org.junit.Rule
import org.junit.Test

class PilotReportScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun tmpTest() {
    val pilot =
        Pilot(
            "id",
            "name",
            "email",
            "password",
            AvailabilityCalendar(),
            FlightGroupCalendar(),
            setOf(RoleType.PILOT),
            BalloonQualification.MEDIUM)
    val location = Location(UNSET_ID, LatLng(0.2, 0.3))
    val time = Time(0L)
    PilotReport(
        "id", pilot, 1, Time(200L), location, location, time, time, time, 1L, "comments", mapOf())
  }
}
