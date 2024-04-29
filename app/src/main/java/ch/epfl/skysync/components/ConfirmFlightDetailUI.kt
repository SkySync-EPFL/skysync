package ch.epfl.skysync.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.ui.theme.lightOrange
import java.time.LocalDate
import java.time.LocalTime


@Composable
fun ConfirmFlightDetail(originalFlight: Flight, confirmedFlight: ConfirmedFlight, backClick: ()->Unit,paddingValues: PaddingValues){
    Column(
        modifier = Modifier.padding(PaddingValues())
    ) {
        Header(backClick = backClick, title = "Confirm Flight")
        ConfirmFlightDetailBody(originalFlight = originalFlight, confirmedFlight = confirmedFlight)
    }
}

@Composable
@Preview
fun ConfirmFlightDetailPreview(){
    val team = Team(listOf(Role(RoleType.PILOT),Role(RoleType.CREW)))
    val ogFlight =
        PlannedFlight(
            id = "1",
            nPassengers = 2,
            flightType = FlightType.DISCOVERY,
            team = Team(listOf()),
            balloon = null,
            basket = null,
            date = LocalDate.now(),
            timeSlot = TimeSlot.AM,
            vehicles = listOf()
        )
    val confirmedFlight =
        ConfirmedFlight(
            id = "1",
            nPassengers = 2,
            flightType = FlightType.DISCOVERY,
            team = team,
            balloon = Balloon(name = "name", qualification = BalloonQualification.LARGE),
            basket = Basket(name = "name", hasDoor = false),
            date = LocalDate.now(),
            timeSlot = TimeSlot.AM,
            vehicles = listOf(),
            remarks = listOf(),
            meetupTimeTeam = LocalTime.MIN,
            departureTimeTeam = LocalTime.MIDNIGHT,
            meetupTimePassenger = LocalTime.NOON,
            meetupLocationPassenger = "idk"
        )
    ConfirmFlightDetail(originalFlight = ogFlight, confirmedFlight = confirmedFlight, backClick = {}, paddingValues = PaddingValues(0.dp))
}
@Composable
fun ConfirmFlightDetailBody(originalFlight: Flight,confirmedFlight: ConfirmedFlight){
    val idBool = confirmedFlight.id == originalFlight.id
    val nbOfPaxBool = confirmedFlight.nPassengers == originalFlight.nPassengers
    val flightTypeBool = confirmedFlight.flightType == originalFlight.flightType
    val teamBool = confirmedFlight.team == originalFlight.team
    val balloonBool = confirmedFlight.balloon == originalFlight.balloon
    val basketBool = confirmedFlight.basket == originalFlight.basket
    val dateBool = confirmedFlight.date == originalFlight.date
    val timeBool = confirmedFlight.timeSlot == originalFlight.timeSlot
    val vehicleBool = confirmedFlight.vehicles == originalFlight.vehicles
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(enabled = true, state = rememberScrollState())
            .testTag("body")
    ) {
        TitledText(padding = 16.dp, title = "ID", value = confirmedFlight.id, color = if(idBool) Color.Black else lightOrange, underLine = !idBool)
        TitledText(padding = 16.dp, title = "Number Of Pax" , value = "${confirmedFlight.nPassengers}", color = if(nbOfPaxBool) Color.Black else lightOrange, underLine = !nbOfPaxBool)
        TitledText(padding = 16.dp, title = "Flight Type", value = confirmedFlight.flightType.name, color = if(flightTypeBool) Color.Black else lightOrange, underLine = !flightTypeBool)
        if(confirmedFlight.team.roles.isEmpty()){
            EmptyListText("Team")
        }
        else{
            TeamText(team = confirmedFlight.team, bool = teamBool)
        }
        TitledText(padding = 16.dp, title = "Balloon", value = confirmedFlight.balloon.name, color = if(balloonBool) Color.Black else lightOrange, underLine = !balloonBool)
        TitledText(padding = 16.dp, title = "Basket", value = confirmedFlight.basket.name, color = if(basketBool) Color.Black else lightOrange, underLine = !basketBool)
        TitledText(padding = 16.dp, title = "Date", value = confirmedFlight.date.toString(), color = if(dateBool) Color.Black else lightOrange, underLine = !dateBool)
        TitledText(padding = 16.dp, title = "Time Slot", value = confirmedFlight.timeSlot.toString(), if(timeBool) Color.Black else lightOrange, underLine = !timeBool)
        if (confirmedFlight.vehicles.isEmpty()){
            EmptyListText("Vehicle")
        }
        else{
            VehicleText(vehicle = confirmedFlight.vehicles, bool = vehicleBool) }
        if(confirmedFlight.remarks.isEmpty()){
            EmptyListText("Remarks")
        }
        else{
            RemarkText(remarks = confirmedFlight.remarks, bool = false)
        }
        TitledText(padding = 16.dp, title = "Flight Color", value = confirmedFlight.color.name, color = lightOrange, underLine = true)
        TitledText(padding = 16.dp, title = "Meetup Time (Team)", value = confirmedFlight.meetupTimeTeam.toString(), color = lightOrange, underLine = true)
        TitledText(padding = 16.dp, title = "Departure Time (Team)", value = confirmedFlight.departureTimeTeam.toString(), color = lightOrange, underLine = true)
        TitledText(padding = 16.dp, title = "Meetup Time (Passenger)", value = confirmedFlight.meetupTimePassenger.toString(), color = lightOrange, underLine = true)
        TitledText(padding = 16.dp, title = "Meetup Location (Passenger)", value = confirmedFlight.meetupLocationPassenger, color = lightOrange, underLine = true)
    }
}
@Composable
fun TeamText(team: Team, bool: Boolean){
    Text(
        text = "Team",
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        style = MaterialTheme.typography.headlineSmall,
    )
    Spacer(modifier = Modifier.padding(4.dp))
    Text(
        text = RoleType.PILOT.name,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        color = if(bool) Color.DarkGray else lightOrange,
        textDecoration = if (!bool) TextDecoration.Underline else TextDecoration.None,
    )
    var index = 0
    for(i in team.roles){
        if(i.roleType == RoleType.PILOT){
            Spacer(modifier = Modifier.padding(4.dp))
            OutlinedTextField(
                value = i.assignedUser?.firstname ?: "No Name",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .testTag(RoleType.PILOT.name + "$index"),
                onValueChange = { },
                enabled = false,
                colors = TextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    disabledContainerColor = Color.White
                )
            )
            index += 1
        }
    }
    Spacer(modifier = Modifier.padding(8.dp))
    Text(
        text = RoleType.CREW.name,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        color = if(bool) Color.DarkGray else lightOrange,
        textDecoration = if (!bool) TextDecoration.Underline else TextDecoration.None,
    )
    index = 0
    for(i in team.roles){
        if(i.roleType == RoleType.CREW){
            Spacer(modifier = Modifier.padding(4.dp))
            OutlinedTextField(
                value = i.assignedUser?.firstname ?: "No Name",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .testTag(RoleType.CREW.name + "$index"),
                onValueChange = { },
                enabled = false,
                colors = TextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    disabledContainerColor = Color.White
                )
            )
            index += 1
        }
    }
    Spacer(modifier = Modifier.padding(12.dp))
}
@Composable
fun VehicleText(vehicle: List<Vehicle>, bool: Boolean){
    Text(
        text = "Vehicle",
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = if(bool) Color.Black else lightOrange,
        style = MaterialTheme.typography.headlineSmall,
        textDecoration = if (!bool) TextDecoration.Underline else TextDecoration.None,
    )
    Spacer(modifier = Modifier.padding(4.dp))
    var index = 0
    for(i in vehicle){
            Spacer(modifier = Modifier.padding(4.dp))
            OutlinedTextField(
                value = i.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("vehicle $index"),
                onValueChange = { },
                enabled = false,
                colors = TextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    disabledContainerColor = Color.White
                )
            )
        }
    Spacer(modifier = Modifier.padding(12.dp))
}
@Composable
fun RemarkText(remarks: List<String>, bool: Boolean){
    Text(
        text = "Remarks",
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = if(bool) Color.Black else lightOrange,
        style = MaterialTheme.typography.headlineSmall,
        textDecoration = if (!bool) TextDecoration.Underline else TextDecoration.None,
    )
    Spacer(modifier = Modifier.padding(4.dp))
    for(i in remarks){
        Spacer(modifier = Modifier.padding(4.dp))
        OutlinedTextField(
            value = i,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("remarks"),
            onValueChange = { },
            enabled = false,
            colors = TextFieldDefaults.colors(
                disabledTextColor = Color.Black,
                disabledContainerColor = Color.White

            )
        )
    }
    Spacer(modifier = Modifier.padding(12.dp))
}
@Composable
fun EmptyListText(title: String){
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = Color.Black,
        style = MaterialTheme.typography.headlineSmall,
    )
    Spacer(modifier = Modifier.padding(4.dp))
    OutlinedTextField(
        value = "No Data",
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag(title),
        onValueChange = { },
        enabled = false,
        colors = TextFieldDefaults.colors(
            disabledTextColor = Color.Black,
            disabledContainerColor = Color.White
        )
    )
    Spacer(modifier = Modifier.padding(12.dp))
}