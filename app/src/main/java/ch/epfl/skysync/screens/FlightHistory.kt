package ch.epfl.skysync.screens

import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.R
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.BASE_ROLES
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.ui.theme.lightOrange
import java.time.LocalDate

@Composable
fun FlightHistoryScreen(navController: NavHostController) {
  val allFlights: MutableList<Flight> = remember {
    mutableStateListOf(
        PlannedFlight(
            id = UNSET_ID,
            nPassengers = 0,
            team = Team(Role.initRoles(BASE_ROLES)),
            flightType = FlightType.DISCOVERY,
            balloon = Balloon("Balloon", BalloonQualification.MEDIUM),
            basket = Basket("Basket", true),
            date = LocalDate.now(),
            timeSlot = TimeSlot.AM,
            vehicles = emptyList()))
  }
  Scaffold(topBar = { CustomTopAppBar(navController = navController, title = "Flight History") }) { padding ->
    Column(modifier = Modifier.fillMaxSize().padding(padding)){
      if (allFlights.isEmpty()) {
        Text(modifier = Modifier.padding(padding), text = "No flights available")
      } else{
        Row(modifier = Modifier.padding(16.dp)){
          //TODO put a searchBar here ?
          TextButton(
            modifier = Modifier.fillMaxWidth().weight(1f),
            shape = RoundedCornerShape(topStart = 5.dp, bottomStart = 5.dp),
            onClick = { },
            content = {
              Text(text = "Date", textAlign = TextAlign.Center)
              Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Sort")
                      },
            colors = ButtonDefaults.buttonColors(containerColor = lightOrange, contentColor = Color.DarkGray),
          )
          TextButton(
            modifier = Modifier.fillMaxWidth().weight(1f),
            shape = RectangleShape,
            onClick = { },
            content = {
              Text(text = "Date", textAlign = TextAlign.Center)
              Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Sort")
                      },
            colors = ButtonDefaults.buttonColors(containerColor = lightOrange, contentColor = Color.DarkGray),
            )
          Button(
            modifier = Modifier.fillMaxWidth().weight(1f),
            shape = RoundedCornerShape(topEnd = 5.dp, bottomEnd = 5.dp),
            onClick = { },
            content = {
              Text(text = "Date", textAlign = TextAlign.Center)
              Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Sort")
              },
            colors = ButtonDefaults.buttonColors(containerColor = lightOrange, contentColor = Color.DarkGray),
            )
        }
        LazyColumn() {
          items(allFlights) { flight ->
            HistoryRow(flight)
          }
        }
      }
    }
  }
}

@Composable
fun HistoryRow(flight: Flight) {
  Row(modifier = Modifier
    .fillMaxWidth()
    .padding(horizontal = 16.dp)
    .border(1.dp, Color.Gray, RoundedCornerShape(5.dp))){
    Text(modifier = Modifier.padding(16.dp).fillMaxWidth(), text = flight.date.toString()) }
}

@Preview
@Composable
fun FlightHistoryScreenPreview() {
  FlightHistoryScreen(navController = rememberNavController())
}
