package ch.epfl.skysync.model

import ch.epfl.skysync.dataModels.calendarModels.Availability
import ch.epfl.skysync.dataModels.calendarModels.AvailabilityCalendar
import ch.epfl.skysync.dataModels.calendarModels.AvailabilityStatus
import ch.epfl.skysync.dataModels.calendarModels.FlightCalendar
import ch.epfl.skysync.dataModels.calendarModels.TimeSlot
import ch.epfl.skysync.dataModels.flightModels.BalloonQualification
import ch.epfl.skysync.dataModels.userModels.Admin
import ch.epfl.skysync.dataModels.userModels.Crew
import ch.epfl.skysync.dataModels.userModels.Pilot
import ch.epfl.skysync.dataModels.userModels.UNSET_ID
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

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
