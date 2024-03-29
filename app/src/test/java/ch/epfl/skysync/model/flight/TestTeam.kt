package ch.epfl.skysync.model.flight

import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.user.Crew
import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TestTeam {
  val testUser1 = Crew("jo", "blunt", UNSET_ID, AvailabilityCalendar(), FlightGroupCalendar())
  val testUser2 = Crew("peter", "brown", UNSET_ID, AvailabilityCalendar(), FlightGroupCalendar())

  @Test
  fun `isComplete() returns true if all roles assigned`() {
    val team1 =
        Team(listOf(Role(RoleType.CREW, testUser1), Role(RoleType.OXYGEN_MASTER, testUser2)))
    assertTrue(team1.isComplete())
    val team2 = Team(listOf(Role(RoleType.CREW, testUser1)))
    assertTrue(team2.isComplete())
  }

  @Test
  fun `isComplete() returns false if no role present`() {
    val team = Team(listOf())
    assertFalse(team.isComplete())
  }

  @Test
  fun `isComplete() returns false not all roles assigned`() {
    val unassignedCrewRole = Role(RoleType.CREW)
    val team1 = Team(listOf(Role(RoleType.CREW, testUser1), unassignedCrewRole))
    assertFalse(team1.isComplete())
  }

  @Test
  fun `addRoles() returns team with added roles`() {
    val role1 = Role(RoleType.OXYGEN_MASTER)
    val role2 = Role(RoleType.CREW)
    val team = Team(listOf(role1))
    val expandedTeam = team.addRoles(listOf(role2))
    assertEquals(expandedTeam.roles.size, 2)
    assertTrue(expandedTeam.roles.any { it.roleType == role1.roleType })
    assertTrue(expandedTeam.roles.any { it.roleType == role2.roleType })
  }

  @Test
  fun `addRoles() returns team with added duplicate roles`() {
    val role1 = Role(RoleType.OXYGEN_MASTER)
    val team = Team(listOf(role1))
    val expandedTeam = team.addRoles(listOf(role1, role1))
    assertEquals(expandedTeam.roles.size, 3)
    assertTrue(expandedTeam.roles.all { it.roleType == role1.roleType })
  }
}
