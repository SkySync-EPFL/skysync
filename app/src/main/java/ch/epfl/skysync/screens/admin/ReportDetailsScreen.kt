package ch.epfl.skysync.screens.admin

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import ch.epfl.skysync.components.DisplaySingleMetric
import ch.epfl.skysync.components.LargeTitle
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import ch.epfl.skysync.models.reports.Report
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.components.DisplayListOfMetrics
import ch.epfl.skysync.components.HeaderTitle
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.database.DateUtility
import ch.epfl.skysync.database.UserRole
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.Availability
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.calendar.getTimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.flight.FlightColor
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.location.LocationPoint
import ch.epfl.skysync.models.reports.CrewReport
import ch.epfl.skysync.models.reports.PilotReport
import ch.epfl.skysync.models.user.Crew
import ch.epfl.skysync.models.user.Pilot
import ch.epfl.skysync.models.user.TempUser
import ch.epfl.skysync.viewmodel.FinishedFlightsViewModel
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun ReportDetailsScreen(flightId: String,finishedFlightsViewModel: FinishedFlightsViewModel,isAdmin : Boolean,userId: String,navController: NavHostController) {
    val flight by finishedFlightsViewModel.getFlight(flightId).collectAsStateWithLifecycle()
    val reportId by finishedFlightsViewModel.flightReports.collectAsStateWithLifecycle()
    val users by finishedFlightsViewModel.flightReportsUsers.collectAsStateWithLifecycle()
    if ((flight == null )||reportId==null) {
        LoadingComponent(isLoading = true, onRefresh = {}) {}
    }

    else {
        val defaultPadding = 16.dp

        val display = isAdmin || reportId!!.any{
            (it.author==userId && it.id ==flightId)
        }
        Column() {
            CustomTopAppBar(navController, "Report")
            Log.d("azdkqf",if(display) "display" else "no")
            if (display) {
                LazyColumn(

                    modifier = Modifier
                        .padding(8.dp)
                        .testTag("FlightDetailLazyColumn")
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(defaultPadding)
                ) {

                    items(reportId!!) { report ->
                        if (isAdmin || report.author == userId) {

                            users?.filter { user ->
                                (user.id == report.author && report.id ==flightId)
                            }?.forEach { user ->
                                DisplayListOfMetrics(user.name(), listOf(report.comments))
                            }


                        }

                    }
                }

            }
            else {
                Text("No reports to see right now")
            }
        }

        }
    }