package ch.epfl.skysync.model.flight

import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.FlightStatus
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
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
class TestPlannedFlight {
  lateinit var user: User
  lateinit var initFlight: PlannedFlight

  @Before
  fun setUp() {
    user =
        Crew(
            id = "1",
            firstname = "Paul",
            lastname = "Panzer",
            email = "paul.panzer@gmail.com",
        )

    initFlight =
        PlannedFlight(
            id = "1",
            nPassengers = 2,
            flightType = FlightType.DISCOVERY,
            balloon = Balloon("QQP", BalloonQualification.LARGE, ""),
            basket = Basket("basket 1", hasDoor = false),
            date = LocalDate.of(2024, 4, 1),
            timeSlot = TimeSlot.AM,
            vehicles = listOf(Vehicle("sprinter 1")))
  }

  @Test
  fun `getFlightStatus returns correct status`() {
    val plannedFlight = initFlight
    assertEquals(plannedFlight.getFlightStatus(), FlightStatus.IN_PLANNING)

    val readyFlight = initFlight.copy(team = Team(listOf(Role(RoleType.PILOT, user))))
    assertEquals(readyFlight.getFlightStatus(), FlightStatus.READY_FOR_CONFIRMATION)
  }

  @Test
  fun `addRoles adds a new role of a new roleType to the team`() {
    val plannedFlight = initFlight.copy(team = Team(listOf()))
    assertEquals(plannedFlight.team.roles.size, 0)
    val roleTypeToAdd = RoleType.PILOT
    val plannedFlightWithRole = plannedFlight.addRoles(listOf(RoleType.PILOT))
    assertEquals(plannedFlightWithRole.team.roles.size, 1)
    plannedFlightWithRole.team.roles.forEach { assertEquals(it.roleType, roleTypeToAdd) }
  }

  @Test
  fun `addRoles adds a role of a an already existing roleType to the team`() {
    val roleTypeToAdd = RoleType.PILOT
    val plannedFlight = initFlight.copy(team = Team(listOf(Role(roleTypeToAdd))))
    val plannedFlightWithRole = plannedFlight.addRoles(listOf(roleTypeToAdd))
    assertEquals(plannedFlightWithRole.team.roles.size, 2)
    plannedFlightWithRole.team.roles.forEach { assertEquals(it.roleType, roleTypeToAdd) }
  }

  @Test
  fun `readyToBeConfirmed returns false if not yet ready`() {
    assertFalse(initFlight.readyToBeConfirmed())
    val readyFlight = initFlight.copy(team = Team(listOf(Role(RoleType.PILOT, user))))
    val plannedFlightNoBasket = readyFlight.copy(basket = null)
    assertFalse(plannedFlightNoBasket.readyToBeConfirmed())

    val plannedFlightNoBalloon = readyFlight.copy(balloon = null)
    assertFalse(plannedFlightNoBalloon.readyToBeConfirmed())

    val plannedFlightNoVehicles = readyFlight.copy(vehicles = emptyList())
    assertFalse(plannedFlightNoVehicles.readyToBeConfirmed())

    val multipleFaults = plannedFlightNoBasket.copy(balloon = null, vehicles = emptyList())
    assertFalse(multipleFaults.readyToBeConfirmed())
  }

  @Test
  fun `readyToBeConfirmed returns true if ready`() {
    val completeTeam =
        Team(
            listOf(
                Role(RoleType.PILOT, user),
            ))
    val readyFlight = initFlight.copy(team = completeTeam)
    assertTrue(readyFlight.readyToBeConfirmed())
  }
}
