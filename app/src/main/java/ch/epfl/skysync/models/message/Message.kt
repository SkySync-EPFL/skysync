package ch.epfl.skysync.models.message

import ch.epfl.skysync.models.UNSET_ID
import java.util.Date

data class Message(
    val id: String = UNSET_ID,
    val date: Date,
    val content: String,
)
