package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.Schema
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.message.Message
import ch.epfl.skysync.models.user.Admin
import com.google.firebase.firestore.DocumentId
import java.util.Date

val UNSET_USER =
    Admin(
        id = UNSET_ID,
        firstname = "",
        lastname = "",
        email = "",
    )

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
        UNSET_USER.copy(id = userId!!),
        date!!,
        content!!,
    )
  }

  companion object {
    fun fromModel(groupId: String, model: Message): MessageSchema {
      return MessageSchema(model.id, groupId, model.user.id, model.date, model.content)
    }
  }
}
