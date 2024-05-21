package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.Schema
import ch.epfl.skysync.models.flight.FlightColor
import ch.epfl.skysync.models.message.MessageGroup
import com.google.firebase.firestore.DocumentId

data class MessageGroupSchema(
    @DocumentId val id: String? = null,
    val name: String? = null,
    val color: FlightColor? = null,
    val userIds: List<String>? = null,
) : Schema<MessageGroup> {
  override fun toModel(): MessageGroup {
    return MessageGroup(
        id!!,
        name!!,
        color!!,
        userIds!!.toSet(),
        listOf(),
    )
  }

  companion object {
    fun fromModel(model: MessageGroup): MessageGroupSchema {
      return MessageGroupSchema(
          model.id,
          model.name,
          model.color,
          model.userIds.toList(),
      )
    }
  }
}
