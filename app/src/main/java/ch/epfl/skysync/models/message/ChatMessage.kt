package ch.epfl.skysync.models.message

data class ChatMessage(
    val message: Message,
    val messageType: MessageType,
)
