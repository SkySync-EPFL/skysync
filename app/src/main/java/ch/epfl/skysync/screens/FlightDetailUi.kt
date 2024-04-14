package ch.epfl.skysync.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.user.Crew
import ch.epfl.skysync.models.user.Pilot
import ch.epfl.skysync.ui.theme.lightOrange
import java.time.LocalDate

@Composable
fun FlightDetailUi(
    BackClick: () -> Unit,
    DeleteClick: () -> Unit,
    EditClick: () -> Unit,
    ConfirmClick: () -> Unit,
    padding: PaddingValues,
    flight: PlannedFlight
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        FlightDetailHead(BackClick = BackClick)
        FlightdetailBody(flight)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            FlightDetailBottom(DeleteClick, EditClick, ConfirmClick, padding)
        }
    }

}

@Composable
@Preview
fun FlightDetailUiPreview() {
    val dummyFlight =
        PlannedFlight(
            UNSET_ID,
            1,
            Team(listOf()),
            FlightType.FONDUE,
            null,
            null,
            LocalDate.now(),
            TimeSlot.AM,
            listOf())
    FlightDetailUi(
        BackClick = {},
        DeleteClick = {},
        EditClick = {},
        ConfirmClick = {},
        padding = PaddingValues(0.dp, 0.dp, 0.dp, 0.dp),
        dummyFlight
        )
}

@Composable
fun FlightDetailHead(BackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    )
    {
        IconButton(
            onClick = BackClick,
            modifier = Modifier.fillMaxWidth(0.2f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = lightOrange)
                Text(text = "Back", color = lightOrange)
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth(0.75f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "Flight Detail",
                fontSize = 20.sp,
            )
        }

    }
}

@Composable
fun FlightdetailBody( flight: PlannedFlight){
    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ){
        Spacer(modifier = Modifier.fillMaxHeight(0.05f))
        TextBar(textLeft = "Flight Id", textRight = flight.id)
        TextBar(textLeft = "Number Of Pax", textRight = flight.nPassengers.toString())
        TeamRolesList(team = flight.team)
        TextBar(textLeft = "Flight ", textRight = flight.flightType.name)
        TextBar(textLeft = "Ballon", textRight = flight.balloon?.name ?: "None")
        TextBar(textLeft = "Basket", textRight = flight.basket?.name ?: "None")
        TextBar(textLeft = "Date", textRight = flight.date.toString())
        TextBar(textLeft = "Time Slot", textRight = flight.timeSlot.name)
        VehicleListText(vehicle = flight.vehicles)
    }
}

@Composable
fun FlightDetailBottom(DeleteClick: () -> Unit, EditClick: () -> Unit, ConfirmClick: () -> Unit,padding: PaddingValues){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                bottom = padding.calculateBottomPadding()
            ),
        contentAlignment = Alignment.BottomCenter)
    {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = DeleteClick,
                modifier = Modifier.fillMaxWidth(0.25f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(
                    text = "Delete",
                    color = Color.Black
                )
            }
            Button(
                onClick = EditClick,
                modifier = Modifier.fillMaxWidth(1 / 3f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)
            ) {
                Text(
                    text = "Edit",
                    color = Color.Black
                )
            }
            Button(
                onClick = ConfirmClick,
                modifier = Modifier.fillMaxWidth(1 / 2f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
            ) {
                Text(
                    text = "Confirm",
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun TextBar(textLeft: String, textRight: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween)
    {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.5f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Text(
                text = textLeft,
                fontSize = 15.sp)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = textRight,
                fontSize = 15.sp)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}
@Composable
fun TeamRolesList(team: Team) {
    LazyColumn {
        items(team.roles.size) { index ->
            val role = team.roles[index]
            TextBar(textLeft = role.roleType.name, "Idk")
        }
    }
}
@Composable
fun VehicleListText(vehicle: List<Vehicle>) {
    LazyColumn {
        items(vehicle.size) { index ->
            val car = vehicle[index]
            TextBar(textLeft = "Vehicle $index", car.name )
        }
    }
}



