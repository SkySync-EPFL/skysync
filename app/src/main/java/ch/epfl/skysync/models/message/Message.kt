package ch.epfl.skysync.models.message

import ch.epfl.skysync.models.UNSET_ID
import java.util.Date

/**
 * Represents a message
 *
 * @param userId The ID of the user who sent the message
 * @param date Date at which the message was sent
 * @param content The content of the message
 */
data class Message(
    val id: String = UNSET_ID,
    val userId: String,
    val date: Date,
    val content: String,
)
