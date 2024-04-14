package ch.epfl.skysync.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import ch.epfl.skysync.models.flight.Team
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
    padding: PaddingValues
){
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        FlightDetailHead(BackClick = BackClick)
        FlightdetailBody(LocalDate.now(), "1", 1, Team(listOf()))
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
    FlightDetailUi(
        BackClick = {},
        DeleteClick = {},
        EditClick = {},
        ConfirmClick = {},
        padding = PaddingValues(0.dp, 0.dp, 0.dp, 0.dp))
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
fun FlightdetailBody(date: LocalDate,flightId: String, nPassengers: Int,pilots : List<Pilot>, crews: List<Crew>){
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ){
        TextBar("Date", date.toString())
        TextBar("flightId", flightId)
        TextBar("nPassengers", nPassengers.toString())
    }
}

@Composable
fun FlightDetailBottom(DeleteClick: () -> Unit, EditClick: () -> Unit, ConfirmClick: () -> Unit,padding: PaddingValues){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                bottom = padding.calculateBottomPadding()),
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
            Text(textLeft)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(textRight)
        }
    }
}


