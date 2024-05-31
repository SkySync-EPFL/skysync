package ch.epfl.skysync.models.user

import androidx.compose.ui.graphics.Color
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.ui.theme.Purple40
import ch.epfl.skysync.ui.theme.lightViolet

data class Crew(
    override val id: String = UNSET_ID,
    override val firstname: String,
    override val lastname: String,
    override val email: String,
    override val roleTypes: Set<RoleType> = setOf(RoleType.CREW),
) : User {
  override fun addRoleType(roleType: RoleType): Crew {
    return this.copy(roleTypes = roleTypes + roleType)
  }

    override fun displayRoleName(): String = "Crew"
    override fun getThemeColor(): Color = Purple40

}
