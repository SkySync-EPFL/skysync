package ch.epfl.skysync.models.message

/**
 * Represents a chat message.
 *
 * @property message The message content.
 * @property messageType The type of the message.
 */
data class ChatMessage(
    val message: Message,
    val messageType: MessageType,
)
