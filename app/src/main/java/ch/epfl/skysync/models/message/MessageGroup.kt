package ch.epfl.skysync.models.message

import ch.epfl.skysync.models.UNSET_ID

/**
 * Represents a message group
 *
 * @param userIds The list of IDs of the users in the group, this can be useful to add/remove users
 *   to/from the group
 * @param messages A list of messages sent in the group
 */
data class MessageGroup(
    val id: String = UNSET_ID,
    val userIds: Set<String> = setOf(),
    val messages: List<Message> = listOf()
)
