package ch.epfl.skysync.screens.reports

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.viewmodel.FinishedFlightsViewModel

/**
 * The screen to display the report details
 *
 * @param flightId The flight id
 * @param finishedFlightsViewModel The view model
 * @param isAdmin Whether the user is an admin
 * @param userId The user id
 * @param navController The navigation controller
 */
@Composable
fun ReportDetailsScreen(
    flightId: String,
    finishedFlightsViewModel: FinishedFlightsViewModel,
    isAdmin: Boolean,
    userId: String,
    navController: NavHostController
) {
  val flight by finishedFlightsViewModel.getFlight(flightId).collectAsStateWithLifecycle()
  val reportIds by finishedFlightsViewModel.flightReports.collectAsStateWithLifecycle()
  val users by finishedFlightsViewModel.flightReportsUsers.collectAsStateWithLifecycle()
  if ((flight == null) || reportIds == null) {
    LoadingComponent(isLoading = true, onRefresh = {}) {}
  } else {

    val display = (isAdmin && reportIds!!.isNotEmpty()) || reportIds!!.any { (it.author == userId) }
    Column() {
      CustomTopAppBar(navController, "Report")
      reportIds!!.forEach { report -> Log.d("report", report.author) }
      if (display) {

        ReportDetail(
            users, finishedFlightsViewModel.reportList(reportIds, isAdmin), isAdmin, userId)
      } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(PaddingValues(0.dp)).testTag("NoReports"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "No reports to see right now", fontSize = 24.sp)
            }
      }
    }
  }
}
