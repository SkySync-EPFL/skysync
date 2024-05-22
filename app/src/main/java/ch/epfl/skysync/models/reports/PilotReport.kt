package ch.epfl.skysync.models.reports

import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.location.LocationPoint
import ch.epfl.skysync.models.user.Pilot
import java.util.Date

data class PilotReport(
    override val id: String = UNSET_ID,
    override val author: String,
    val effectivePax: Int,
    val takeOffTime: Date,
    val takeOffLocation: LocationPoint,
    val landingLocation: LocationPoint,
    val landingTime: Date,
    override val begin: Date,
    override val end: Date,
    override val pauseDuration: Int, // in milliseconds
    override val comments: String,
) : Report
