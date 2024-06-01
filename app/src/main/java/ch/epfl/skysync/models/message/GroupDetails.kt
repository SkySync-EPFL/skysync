package ch.epfl.skysync.models.message

import ch.epfl.skysync.models.flight.FlightColor

/**
 * Give the details of a ChatGroup
 *
 * @param color Color of the confirmed flight that will appear on the icon
 */
data class GroupDetails(
    val id: String,
    val name: String,
    val color: FlightColor,
    val lastMessage: Message?,
)
