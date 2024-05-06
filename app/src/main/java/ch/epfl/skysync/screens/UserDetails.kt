package ch.epfl.skysync.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.user.User
import ch.epfl.skysync.navigation.AdminBottomBar
import ch.epfl.skysync.ui.theme.lightOrange
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PersonalFlightHistory(
    flights: List<Flight>,
    paddingValues: PaddingValues,
    onFlightClick: (String) -> Unit
) {
  Column(
      modifier =
          Modifier.fillMaxSize()
              .padding(
                  top = paddingValues.calculateTopPadding() + 16.dp,
                  start = 16.dp,
                  end = 16.dp,
                  bottom = 16.dp)) {
        Text(
            text = "Completed Flights",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier =
                Modifier.background(
                        color = lightOrange,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .fillMaxWidth()
                    .padding(16.dp),
            color = Color.White,
            textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(16.dp))
        if (flights == null) {
          LoadingComponent(isLoading = true, onRefresh = { /*TODO*/}) {}
        } else if (flights.isEmpty()) {
          Box(
              modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3f),
              contentAlignment = Alignment.Center) {
                Text(
                    text = "No completed flights",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black)
              }
        } else {
          LazyColumn { items(flights) { flight -> FlightCard(flight, onFlightClick) } }
        }
      }
}

@Composable
fun FlightCard(flight: Flight, onFlightClick: (String) -> Unit) {
  // Card for an individual flight, clickable to navigate to details
  Card(
      modifier =
          Modifier.fillMaxWidth()
              .clickable { onFlightClick(flight.id) }
              .padding(vertical = 4.dp)
              .testTag("flightCard"),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
  ) {
    Surface(modifier = Modifier.fillMaxWidth(), color = flight.getFlightStatus().displayColor) {
      Row(
          modifier = Modifier.fillMaxWidth().padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Start) {
            Text(
                text =
                    flight.date.format(
                        DateTimeFormatter.ofPattern("E\ndd").withLocale(Locale.ENGLISH)),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.alignByBaseline(),
                color = Color.Black)
            // Spacer for horizontal separation
            Spacer(modifier = Modifier.width(16.dp))
            // Column for flight details
            Column(modifier = Modifier.weight(0.7f).padding(start = 16.dp)) {
              // Text for flight type and passenger count
              Text(
                  text = "${flight.flightType.name} - ${flight.nPassengers} pax",
                  fontWeight = FontWeight.Bold,
                  color = Color.Black)
              // Text for flight time slot
              Text(text = flight.timeSlot.toString(), color = Color.Gray)
            }
            Text(
                text = flight.getFlightStatus().toString(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.alignByBaseline(),
                color = Color.Gray)
          }
    }
  }
}

// Scaffold wrapper for the Home Screen
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserDetailsScreen(
    navController: NavHostController,
    allFlights: List<FinishedFlight>,
    user: User
) {
  val userFlights =
      allFlights.filter { flight -> flight.team.roles.any { role -> role.assignedUser == user } }

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = { UserDetailTopAppBar(user = user, onBackClicked = { navController.navigateUp() }) },
      bottomBar = { AdminBottomBar(navController = navController) },
      floatingActionButtonPosition = FabPosition.End,
  ) { paddingValues ->
    // Apply paddingValues to the content to ensure it doesn't overlap with the TopAppBar
    PersonalFlightHistory(userFlights, paddingValues) { selectedFlight ->
      navController.navigate("flight_details/$selectedFlight")
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailTopAppBar(user: User, onBackClicked: () -> Unit) {
  SmallTopAppBar(
      title = {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
          Text(text = "${user.firstname} ${user.lastname}")
        }
      },
      navigationIcon = {
        IconButton(onClick = onBackClicked) {
          Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
        }
      },
      actions = {
        Spacer(modifier = Modifier.width(48.dp)) // Balance the navigation icon
      },
      colors =
          TopAppBarDefaults.smallTopAppBarColors(
              containerColor = Color.White,
              titleContentColor = Color.Black,
              navigationIconContentColor = Color.Black))
}

