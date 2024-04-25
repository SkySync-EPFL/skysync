package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.Schema
import ch.epfl.skysync.models.message.Message
import com.google.firebase.firestore.DocumentId
import java.util.Date

data class MessageSchema(
    @DocumentId val id: String? = null,
    val groupId: String? = null,
    val userId: String? = null,
    val date: Date? = null,
    val content: String? = null,
) : Schema<Message> {
  override fun toModel(): Message {
    return Message(
        id!!,
        userId!!,
        date!!,
        content!!,
    )
  }

  companion object {
    fun fromModel(groupId: String, model: Message): MessageSchema {
      return MessageSchema(model.id, groupId, model.userId, model.date, model.content)
    }
  }
}
