package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.UNSET_ID

/**
 * represents a balloon (envelope)
 *
 * @property name the name id of the balloon
 * @property qualification classification of balloon size
 * @property id: the db id
 */
data class Balloon(
    val name: String,
    val qualification: BalloonQualification,
    val id: String = UNSET_ID
)
