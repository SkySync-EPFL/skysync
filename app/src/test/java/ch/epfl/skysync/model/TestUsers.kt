package ch.epfl.skysync.model

import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.FlightCalendar
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.user.Admin
import ch.epfl.skysync.models.user.Crew
import ch.epfl.skysync.models.user.Pilot
import ch.epfl.skysync.models.user.UNSET_ID
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TestUsers {

  @Before
  fun setUp() {
  }
  @Test
  fun `create Pilot`() {
    val pilot = Pilot("John",
      "Deer",
      UNSET_ID,
      AvailabilityCalendar(),
      FlightCalendar(),
      BalloonQualification.LARGE)

  }
  @Test
  fun `create Crew`() {
    val crew = Crew("John",
      "Deer",
      UNSET_ID,
      AvailabilityCalendar(),
      FlightCalendar())
  }

  @Test
  fun `create Admin`() {
    val admin = Admin("John",
      "Deer",
      UNSET_ID,
      AvailabilityCalendar(),
      FlightCalendar())
  }






}
