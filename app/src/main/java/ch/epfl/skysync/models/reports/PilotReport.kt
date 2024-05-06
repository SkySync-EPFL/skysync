package ch.epfl.skysync.models.reports

import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.location.Location
import ch.epfl.skysync.models.user.Pilot
import java.sql.Time

data class PilotReport(
    val id: String,
    val author: Pilot,
    val effectivePax: Int,
    val takeOffTime: Time,
    val takeOffLocation: Location,
    val landingLocation: Location,
    val landingTime: Time,
    override val begin: Time,
    override val end: Time,
    override val pause: Boolean,
    override val pauseDuration: Long,
    override val comments: String,
    override val vehicleProblems: Map<Vehicle, String>
) : Report
