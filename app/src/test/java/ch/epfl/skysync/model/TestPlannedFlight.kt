package ch.epfl.skysync.model

import ch.epfl.skysync.dataModels.flightModels.Balloon
import ch.epfl.skysync.dataModels.flightModels.BalloonQualification
import ch.epfl.skysync.dataModels.flightModels.Basket
import ch.epfl.skysync.dataModels.flightModels.FlightType
import ch.epfl.skysync.dataModels.flightModels.PlannedFlight
import ch.epfl.skysync.dataModels.flightModels.Team
import ch.epfl.skysync.dataModels.flightModels.Vehicle
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TestPlannedFlight {

  @Test
  fun `planned flight is correctly created`() {

    val testFlight = PlannedFlight(
      1,
      2,
      Team(),
      FlightType.DISCOVERY,
      Balloon("QQP", BalloonQualification.LARGE),
      Basket("basket 1", hasDoor = false),
      LocalDate.now(),
      isMorningFlight = true,
        listOf(Vehicle("sprinter 1"))
    )

    assertTrue(true)
  }
}
