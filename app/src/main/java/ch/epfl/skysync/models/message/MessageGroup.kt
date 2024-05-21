package ch.epfl.skysync.models.message

import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.flight.FlightColor

/**
 * Represents a message group
 *
 * @param userIds The list of IDs of the users in the group, this can be useful to add/remove users
 *   to/from the group
 * @param messages A list of messages sent in the group
 */
data class MessageGroup(
    val id: String = UNSET_ID,
    val name: String,
    val color: FlightColor,
    val userIds: Set<String> = setOf(),
    val messages: List<Message> = listOf()
) {
  fun withNewMessage(message: Message): MessageGroup {
    return this.copy(messages = listOf(message) + messages)
  }
}
