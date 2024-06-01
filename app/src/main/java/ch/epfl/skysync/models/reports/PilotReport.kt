package ch.epfl.skysync.models.reports

import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.location.LocationPoint
import java.util.Date

/**
 * Represents a report filled by a pilot.
 *
 * @property id The ID of the report. By default, it is set to UNSET_ID.
 * @property author The author of the report.
 * @property effectivePax The effective number of passengers.
 * @property takeOffTime The time at which the plane took off.
 * @property takeOffLocation The location at which the plane took off.
 * @property landingLocation The location at which the plane landed.
 * @property landingTime The time at which the plane landed.
 * @property begin The beginning of the report.
 * @property end The end of the report.
 * @property pauseDuration The duration of the pause in milliseconds.
 * @property comments The comments of the report.
 */
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
