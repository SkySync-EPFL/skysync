package ch.epfl.skysync.models.reports

import ch.epfl.skysync.models.UNSET_ID
import java.util.Date

/**
 * Represents a report filled by a crew member.
 *
 * @property id The ID of the report. By default, it is set to UNSET_ID.
 * @property author The author of the report.
 * @property begin The beginning of the report.
 * @property end The end of the report.
 * @property pauseDuration The duration of the pause in milliseconds.
 * @property comments The comments of the report.
 */
data class CrewReport(
    override val id: String = UNSET_ID,
    override val author: String,
    override val begin: Date,
    override val end: Date,
    override val pauseDuration: Int, // in milliseconds
    override val comments: String,
) : Report
