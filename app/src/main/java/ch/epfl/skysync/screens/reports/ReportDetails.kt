package ch.epfl.skysync.screens.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import ch.epfl.skysync.components.DisplaySingleMetric
import ch.epfl.skysync.components.HeaderTitle
import ch.epfl.skysync.database.DateUtility
import ch.epfl.skysync.models.reports.CrewReport
import ch.epfl.skysync.models.reports.PilotReport
import ch.epfl.skysync.models.reports.Report
import ch.epfl.skysync.models.user.User

@Composable
fun ReportDetail(
    users: List<User>?,
    reportId: List<Report>?,
    isAdmin: Boolean,
    userId: String,
    flightId: String
) {
  val defaultPadding = 16.dp
  LazyColumn(
      modifier = Modifier.padding(8.dp).testTag("FlightDetailLazyColumn").fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(defaultPadding)) {
        items(reportId!!) { report ->
          if (isAdmin || report.author == userId) {

            users
                ?.filter { user -> (user.id == report.author) }
                ?.forEach { user ->
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    HeaderTitle(title = user.name(), padding = defaultPadding, color = Color.Black)
                  }
                  when (report) {
                    is CrewReport -> {
                      DisplaySingleMetric("Comments", report.comments)
                      DisplaySingleMetric(
                          metric = "Begin",
                          value =
                              DateUtility.localDateToString(
                                  DateUtility.dateToLocalDate(report.begin)))
                      DisplaySingleMetric(
                          metric = "End",
                          value =
                              DateUtility.localDateToString(
                                  DateUtility.dateToLocalDate(report.end)))
                    }
                    is PilotReport -> {
                      DisplaySingleMetric("Comments", report.comments)
                      DisplaySingleMetric(
                          metric = "Begin",
                          value =
                              DateUtility.localDateToString(
                                  DateUtility.dateToLocalDate(report.begin)))
                      DisplaySingleMetric(
                          metric = "End",
                          value =
                              DateUtility.localDateToString(
                                  DateUtility.dateToLocalDate(report.end)))
                      DisplaySingleMetric(
                          metric = "Number of passengers on board",
                          value = report.effectivePax.toString())
                      DisplaySingleMetric(
                          metric = "Takeoff location", value = report.takeOffLocation.name)
                      DisplaySingleMetric(
                          metric = "Takeoff time",
                          value =
                              DateUtility.localDateToString(
                                  DateUtility.dateToLocalDate(report.takeOffTime)))
                      DisplaySingleMetric(
                          metric = "Landing location", value = report.landingLocation.name)
                      DisplaySingleMetric(
                          metric = "Landing time",
                          value =
                              DateUtility.localDateToString(
                                  DateUtility.dateToLocalDate(report.landingTime)))
                    }
                  }
                }
          }
        }
      }
}
