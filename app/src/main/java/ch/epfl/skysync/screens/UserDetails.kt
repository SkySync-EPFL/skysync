package ch.epfl.skysync.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.FlightsList
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.user.User
import ch.epfl.skysync.navigation.AdminBottomBar
import ch.epfl.skysync.ui.theme.lightOrange

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
    FlightsList(userFlights, lightOrange, paddingValues, "Completed Flights") { selectedFlight ->
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
