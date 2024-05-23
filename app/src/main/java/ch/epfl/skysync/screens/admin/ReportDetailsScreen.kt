package ch.epfl.skysync.screens.admin

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.screens.reports.ReportDetail
import ch.epfl.skysync.viewmodel.FinishedFlightsViewModel

@Composable
fun ReportDetailsScreen(
    flightId: String,
    finishedFlightsViewModel: FinishedFlightsViewModel,
    isAdmin: Boolean,
    userId: String,
    navController: NavHostController
) {
  val flight by finishedFlightsViewModel.getFlight(flightId).collectAsStateWithLifecycle()
  val reportId by finishedFlightsViewModel.flightReports.collectAsStateWithLifecycle()
  val users by finishedFlightsViewModel.flightReportsUsers.collectAsStateWithLifecycle()
  if ((flight == null) || reportId == null) {
    LoadingComponent(isLoading = true, onRefresh = {}) {}
  } else {

    val display = isAdmin || reportId!!.any { (it.author == userId && it.id == flightId) }
    Column() {
      CustomTopAppBar(navController, "Report")
      if (display) {
        ReportDetail(users, reportId, isAdmin, userId, flightId)
      } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(PaddingValues(0.dp)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "No reports to see right now", fontSize = 24.sp)
            }
      }
    }
  }
}
