package ch.epfl.skysync.model.flight

import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.user.Crew
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TestRole {
  lateinit var unassignedCrewRole: Role
  val testUser = Crew("jo", "blunt", UNSET_ID, AvailabilityCalendar(), FlightGroupCalendar())

  @Before
  fun setUp() {
    unassignedCrewRole = Role(RoleType.CREW)
  }

  @Test
  fun `assign() assigns user to role`() {
    assertEquals(unassignedCrewRole.assignedUser, null)
    val newRole = unassignedCrewRole.assign(testUser)
    assertEquals(testUser, newRole.assignedUser)
    // check that initial remains unchanged
    assertEquals(unassignedCrewRole.assignedUser, null)
  }

  @Test
  fun `isAssigned() returns if assigned or not`() {
    assertFalse(unassignedCrewRole.isAssigned())
    val newRole = unassignedCrewRole.assign(testUser)
    assertTrue(newRole.isAssigned())

    // check that initial remains unchanged
    assertFalse(unassignedCrewRole.isAssigned())
  }

  @Test
  fun `initRoles() returns correct list of roles`() {
    val roleTypeList = listOf<RoleType>(RoleType.CREW, RoleType.OXYGEN_MASTER)
    val roleList: List<Role> = Role.initRoles(roleTypeList)
    assertEquals(roleList.size, roleTypeList.size)
    for (i in roleTypeList) {
      assertTrue(roleList.any { it.isOfRoleType(i) })
    }
  }

  @Test
  fun `isOfRoleType() returns true if of same role type`() {
    val roleType = RoleType.CREW
    val role = Role(roleType)
    assertTrue(role.isOfRoleType(roleType))
  }

  @Test
  fun `isOfRoleType() returns false if not of same role type`() {
    val role = Role(RoleType.OXYGEN_MASTER)
    assertFalse(role.isOfRoleType(RoleType.CREW))
  }
}
