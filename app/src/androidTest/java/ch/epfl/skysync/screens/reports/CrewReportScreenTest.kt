package ch.epfl.skysync.screens.reports

import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.reports.CrewReport
import ch.epfl.skysync.models.user.Crew
import java.sql.Time
import org.junit.Rule
import org.junit.Test

class CrewReportScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun tmpTest() {
    val crew =
        Crew(
            "id",
            "name",
            "email",
            "password",
            AvailabilityCalendar(),
            FlightGroupCalendar(),
            setOf(RoleType.PILOT))
    CrewReport("id", crew, 1, 1, 1, Time(0L), Time(0L), 1L, "comments", mapOf())
  }
}
