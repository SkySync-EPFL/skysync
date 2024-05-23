package ch.epfl.skysync.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import ch.epfl.skysync.components.DisplaySingleMetric
import ch.epfl.skysync.components.LargeTitle
import androidx.compose.material3.Text
import ch.epfl.skysync.models.reports.Report
import androidx.compose.ui.text.font.FontWeight
@Composable
fun ReportDetailsScreen(reportId: List<Report>, padding: PaddingValues) {
    val defaultPadding = 16.dp
    LazyColumn(
        modifier = Modifier.padding(8.dp).weight(1f).testTag("FlightDetailLazyColumn"),
        verticalArrangement = Arrangement.spacedBy(defaultPadding)) {

        reportId.forEach { report ->
            item {
                LargeTitle(title = report.author, padding = defaultPadding, color = Color.Black)
                report.comments.forEach { comment ->
                    Text(text = comment.toString())
                }
            }
        }
    }
}