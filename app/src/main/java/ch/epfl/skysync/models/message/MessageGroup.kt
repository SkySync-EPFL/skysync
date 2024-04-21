package ch.epfl.skysync.models.message

import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.user.User

data class MessageGroup(
    val id: String = UNSET_ID,
    val userIds: Set<String> = setOf(),
    val messages: List<Message> = listOf()
) {

  fun withUser(user: User): MessageGroup {
    return this.copy(userIds = userIds + user.id)
  }

  fun withMessages(messages: List<Message>): MessageGroup {
    return this.copy(messages = messages + messages)
  }

  fun withMessage(message: Message): MessageGroup {
    return this.copy(messages = messages + message)
  }
}
