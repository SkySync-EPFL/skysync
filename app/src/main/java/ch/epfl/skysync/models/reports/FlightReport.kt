package ch.epfl.skysync.models.reports

import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.user.Crew
import java.util.Date

data class FlightReport(
    override val id: String = UNSET_ID,
    override val author: String,
    override val begin: Date,
    override val end: Date,
    override val pauseDuration: Long?, // in milliseconds
    override val comments: String,
    override val vehicleProblems: Map<Vehicle, String>
) : Report