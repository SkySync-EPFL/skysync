package ch.epfl.skysync.models.message

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents a chat message.
 *
 * @property message The message content.
 * @property messageType The type of the message.
 * @property profilePicture The profile picture of the user who sent the message.
 */
data class ChatMessage(
    val message: Message,
    val messageType: MessageType,
    val profilePicture: ImageVector?,
)
