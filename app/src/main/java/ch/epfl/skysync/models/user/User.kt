package ch.epfl.skysync.models.user

import androidx.compose.ui.graphics.Color
import ch.epfl.skysync.models.flight.RoleType

interface User {
  val id: String
  val firstname: String
  val lastname: String
  val email: String
  val roleTypes: Set<RoleType>

  fun addRoleType(roleType: RoleType): User

  fun canAssumeRole(roleType: RoleType): Boolean {
    return roleTypes.contains(roleType)
  }

  fun name(): String = "$firstname $lastname"

  /**
   * displays the name of a particular User class
   */
  fun displayRoleName(): String


  fun getThemeColor(): Color

}
