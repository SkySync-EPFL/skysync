package ch.epfl.skysync.models.reports

import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.user.Crew
import java.sql.Time

data class CrewReport(
    val id: String,
    val author: Crew,
    val littleChampagne: Int,
    val bigChampagne: Int,
    val prestigeChampagne: Int,
    val littleChampagneToFarmer: Boolean,
    val bigChampagneToFarmer: Boolean,
    val prestigeChampagneToFarmer: Boolean,
    override val begin: Time,
    override val end: Time,
    override val pause: Boolean,
    override val pauseDuration: Long,
    override val comments: String,
    override val vehicleProblems: Map<Vehicle, String>
) : Report
