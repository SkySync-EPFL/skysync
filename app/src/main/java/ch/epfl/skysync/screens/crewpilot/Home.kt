package ch.epfl.skysync.screens.crewpilot

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.components.UpcomingFlights
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.user.Admin
import ch.epfl.skysync.navigation.AdminBottomBar
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.Purple40
import ch.epfl.skysync.ui.theme.lightOrange
import ch.epfl.skysync.viewmodel.FlightsViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale


// Scaffold wrapper for the Home Screen
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavHostController, viewModel: FlightsViewModel) {
  val currentFlights by viewModel.currentFlights.collectAsStateWithLifecycle()
      Scaffold(
          modifier = Modifier.fillMaxSize(),
          bottomBar = { BottomBar(navController) },
          floatingActionButton = {
            // Define the FloatingActionButton to create a flight
          },
          floatingActionButtonPosition = FabPosition.End,
      ) { padding ->
        UpcomingFlights(currentFlights, Purple40) { selectedFlight ->
          // Here is where you'd navigate to a new screen. For now, just log a message.
          Log.d("HomeScreen", "Navigating to FlightDetails with id $selectedFlight")

          navController.navigate(Route.FLIGHT_DETAILS + "/${selectedFlight}")
          // Example navigation call: navController.navigate("FlightDetails.id")
        }
      }
    }

// Preview provider for the Home Screen
// @Composable
// @Preview
// fun HomeScreenPreview() {
//  // Preview navigation controller
//  val navController = rememberNavController()
//  // Preview of Home Screen
//  HomeScreen(navController = navController)
// }
