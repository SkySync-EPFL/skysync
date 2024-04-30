package ch.epfl.skysync.models.message

import androidx.compose.ui.graphics.vector.ImageVector

data class ChatMessage(
    val message: Message,
    val messageType: MessageType,
    val profilePicture: ImageVector?,
)
