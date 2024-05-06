package ch.epfl.skysync.models.reports

import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.user.Crew

data class CrewReport(
    val id: String,
    val author: Crew,
    val littleChampagne: Int,
    val bigChampagne: Int,
    val prestigeChampagne: Int,
    val littleChampagneToFarmer: Boolean,
    val bigChampagneToFarmer: Boolean,
    val prestigeChampagneToFarmer: Boolean,
    val vehicleProblems: Map<Vehicle, String>,
    val didTakePause: Boolean,
    val pauseDuration: Long?,
    val comments: String
)
