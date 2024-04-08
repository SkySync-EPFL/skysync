package ch.epfl.skysync.database.schemas
x
import ch.epfl.skysync.database.Schema
import com.google.firebase.firestore.DocumentId

data class FlightMemberSchema(
    @DocumentId val id: String? = null,
    val name: String? = null,
    val hasDoor: Boolean? = null,
) : Schema<FlightMemberSchema> {
  override fun toModel(): FlightMemberSchema {
    return this
  }
}
