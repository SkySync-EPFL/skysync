package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.UNSET_ID

data class Basket(val name: String, val hasDoor: Boolean, val id: String = UNSET_ID)
