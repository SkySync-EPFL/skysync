package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.Schema
import ch.epfl.skysync.models.flight.RoleType
import com.google.firebase.firestore.DocumentId

data class FlightMemberSchema(
    @DocumentId val id: String? = null,
    val userId: String? = null,
    val flightId: String? = null,
    val roleType: RoleType? = null,
) : Schema<FlightMemberSchema> {
  override fun toModel(): FlightMemberSchema {
    return this
  }
}
