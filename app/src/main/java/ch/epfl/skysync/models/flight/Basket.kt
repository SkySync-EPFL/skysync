package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.UNSET_ID

/**
 * Represents a basket for a hot air balloon.
 *
 * This is an immutable data class that holds the name of the basket, whether it has a door, and its
 * ID.
 *
 * @property name The name of the basket.
 * @property hasDoor Whether the basket has a door.
 * @property id The ID of the basket. Defaults to [UNSET_ID] if not provided.
 */
data class Basket(val name: String, val hasDoor: Boolean, val id: String = UNSET_ID)
