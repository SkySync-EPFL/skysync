package ch.epfl.skysync.models.reports

import android.location.Location
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.user.Pilot
import java.util.Date

data class PilotReport(
    val id: String = UNSET_ID,
    override val author: Pilot,
    val effectivePax: Int,
    val takeOffTime: Date,
    val takeOffLocation: Location,
    val landingLocation: Location,
    val landingTime: Date,
    override val begin: Date,
    override val end: Date,
    override val pauseDuration: Long?, // in milliseconds
    override val comments: String,
    override val vehicleProblems: Map<Vehicle, String>
) : Report
