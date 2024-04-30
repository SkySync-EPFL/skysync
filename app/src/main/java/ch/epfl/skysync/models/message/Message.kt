package ch.epfl.skysync.models.message

import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.user.User
import java.util.Date

/**
 * Represents a message
 *
 * @param user The user who sent the message
 * @param date Date at which the message was sent
 * @param content The content of the message
 */
data class Message(
    val id: String = UNSET_ID,
    val user: User,
    val date: Date,
    val content: String,
)
