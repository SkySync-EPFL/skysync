package ch.epfl.skysync.screens.admin

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
import ch.epfl.skysync.models.reports.Report
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import ch.epfl.skysync.components.DisplayListOfMetrics
import ch.epfl.skysync.components.HeaderTitle
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
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun ReportDetailsScreen(reportId: List<Report>, padding: PaddingValues,isAdmin : Boolean,userId: String) {
    val defaultPadding = 16.dp
    LazyColumn(

        modifier = Modifier.padding(8.dp).testTag("FlightDetailLazyColumn").fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(defaultPadding)) {
        item{
            HeaderTitle(title = "Report", padding = defaultPadding, color = Color.Black)
        }
        items(reportId){ report ->
            if(isAdmin||report.id == userId) {
                DisplayListOfMetrics(report.author, listOf(report.comments))
            }
        }
    }
}
@Composable
@Preview
fun ReportDetailsScreenPreview(){
    val crew1 =
        Crew(
            id = "id-crew-1",
            firstname = "crew-1",
            lastname = "lastname-crew-1",
            email = "crew1.bob@skysnc.ch",
        )
    val pilot1 =
        Pilot(
            id = "id-pilot-1",
            firstname = "pilot-1",
            lastname = "lastname-pilot-1",
            email = "pilot1.bob@skysnc.ch",
            qualification = BalloonQualification.LARGE)

    val date1 = LocalDate.of(2024, 8, 14)

    // this the date of flight4, it needs to be today for the InFlightViewModel tests
    val landingTime2 = DateUtility.createDate(date1.year, date1.monthValue, date1.dayOfMonth, 7, 0)
    val takeOffTime2 = DateUtility.createDate(date1.year, date1.monthValue, date1.dayOfMonth, 6, 0)

    val landingLocation1 = LocationPoint(0, 0.0, 0.0, "Landing")
    val takeOffLocation1 = LocationPoint(0, 1.0, 1.0, "Take off")
    var report1 =
        PilotReport(
            id = "1234",
            author = pilot1.id,
            effectivePax = 2,
            takeOffTime = takeOffTime2,
            landingTime = landingTime2,
            takeOffLocation = takeOffLocation1,
            landingLocation = landingLocation1,
            begin = takeOffTime2,
            end = landingTime2,
            pauseDuration = 0,
            comments = "Some comments but 1234",
        )
    var report2 =
        CrewReport(
            id = "5678",
            author = crew1.id,
            begin = takeOffTime2,
            end = landingTime2,
            pauseDuration = 0,
            comments = "Some comments but I also want to thank my dad for all the work he did and the conjunction between the economical despair and the alliance between aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
        )
    ReportDetailsScreen(listOf(report1,report2),PaddingValues(0.dp),true,"1234")
}