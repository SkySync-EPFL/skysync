package ch.epfl.skysync.model.user

import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.user.Admin
import ch.epfl.skysync.models.user.Crew
import ch.epfl.skysync.models.user.Pilot
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TestUsers {

  @Before fun setUp() {}

  @Test
  fun `create Pilot`() {
    val pilot =
        Pilot(
            "John",
            "Deer",
            UNSET_ID,
            AvailabilityCalendar(),
            FlightGroupCalendar(),
            setOf(RoleType.PILOT, RoleType.CREW),
            BalloonQualification.LARGE)

    val pilotWithRoleType = pilot.addRoleType(RoleType.MAITRE_FONDUE)
    assertEquals(false, pilot.canAssumeRole(RoleType.MAITRE_FONDUE))
    assertEquals(true, pilotWithRoleType.canAssumeRole(RoleType.MAITRE_FONDUE))
    assertEquals(
        setOf(RoleType.PILOT, RoleType.CREW, RoleType.MAITRE_FONDUE), pilotWithRoleType.roleTypes)
  }

  @Test
  fun `create Crew`() {
    val crew = Crew("John", "Deer", UNSET_ID, AvailabilityCalendar(), FlightGroupCalendar())
  }

  @Test
  fun `create Admin`() {
    val admin = Admin("John", "Deer", UNSET_ID, AvailabilityCalendar(), FlightGroupCalendar())
  }
}
