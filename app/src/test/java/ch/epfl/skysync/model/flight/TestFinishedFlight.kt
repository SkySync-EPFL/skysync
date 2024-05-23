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
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.location.FlightTrace
import ch.epfl.skysync.models.location.LocationPoint
import ch.epfl.skysync.models.user.Crew
import ch.epfl.skysync.models.user.User
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TestFinishedFlight {

  lateinit var crew: User
  lateinit var finishedFlight: FinishedFlight

  @Before
  fun setUp() {
    crew =
        Crew(
            id = "1",
            firstname = "Paul",
            lastname = "Panzer",
            email = "paul.panzer@gmail.com",
        )

    finishedFlight =
        FinishedFlight(
            nPassengers = 3,
            team = Team(roles = listOf(Role(RoleType.PILOT, pilot1), Role(RoleType.CREW, crew1))),
            flightType = FlightType.FONDUE,
            balloon = Balloon("2", BalloonQualification.LARGE),
            basket = Basket("3", false),
            date = LocalDate.of(2001, 1, 2),
            timeSlot = TimeSlot.PM,
            vehicles = emptyList(),
            id = "1",
            takeOffTime = DateUtility.localDateToDate(LocalDate.of(2001, 1, 1)),
            takeOffLocation = LocationPoint(1, 0.0, 0.1),
            landingTime = DateUtility.localDateToDate(LocalDate.of(2000, 2, 1)),
            landingLocation = LocationPoint(1, 0.0, 0.1),
            flightTime = 1001,
            reportId = emptyList(),
            flightTrace = FlightTrace(UNSET_ID, emptyList()),
            thisFlightStatus = FlightStatus.MISSING_REPORT)
  }

  @Test
  fun `updateFlightStatusForWithCondition returns same instance if status is not changed`() {
    assertEquals(finishedFlight.updateFlightStatus(crew), finishedFlight)
  }
}
