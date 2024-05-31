package ch.epfl.skysync.models.user

import androidx.compose.ui.graphics.Color
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.ui.theme.Purple40
import ch.epfl.skysync.ui.theme.lightViolet

data class Pilot(
    override val id: String = UNSET_ID,
    override val firstname: String,
    override val lastname: String,
    override val email: String,
    override val roleTypes: Set<RoleType> = setOf(RoleType.CREW, RoleType.PILOT),
    val qualification: BalloonQualification,
) : User {

  override fun addRoleType(roleType: RoleType): Pilot {
    return this.copy(roleTypes = roleTypes + roleType)
  }

    override fun displayRoleName() = "Pilot"
    override fun getThemeColor(): Color = Purple40
}
