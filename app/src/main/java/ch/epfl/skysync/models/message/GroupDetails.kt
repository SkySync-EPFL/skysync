package ch.epfl.skysync.models.message

import androidx.compose.ui.graphics.vector.ImageVector
import ch.epfl.skysync.models.flight.FlightColor

data class GroupDetails(
    val id: String,
    val name: String,
    val color: FlightColor,
    val image: ImageVector?,
    val lastMessage: Message?,
)
