package ch.epfl.skysync.models.reports

import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.location.LocationPoint
import ch.epfl.skysync.models.user.Pilot
import java.time.LocalTime
import java.util.Date

data class PilotReport(
    override val id: String = UNSET_ID,
    override val author: Pilot,
    val effectivePax: Int,
    val takeOffTime: LocalTime,
    val takeOffLocation: LocationPoint,
    val landingLocation: LocationPoint,
    val landingTime: LocalTime,
    override val begin: LocalTime,
    override val end: LocalTime,
    override val pauseDuration: Long?, // in milliseconds
    override val comments: String,
    override val vehicleProblems: Map<Vehicle, String>
) : Report
