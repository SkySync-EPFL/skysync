package ch.epfl.skysync.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.viewmodel.UserViewModel

@Composable
fun CalendarScreen(navController: NavHostController,
                   calendarType: String,
                   viewModel: UserViewModel) {
  Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = { BottomBar(navController) }) { padding ->
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween

    ){
        if (calendarType == Route.AVAILABILITY_CALENDAR) {
                showCalendarAvailabilities(navController,padding, viewModel)
            }
        else if (calendarType == Route.PERSONAL_FLIGHT_CALENDAR) {
            //TODO: change to correct view model
                showCalendarAvailabilities(navController, padding, viewModel)
            }
      Row {
          Button(onClick = { /*TODO*/ }) {
              Text("Flights")

          }
          Button(onClick = { /*TODO*/ }) {
              Text("Availabilities")

          }

      }
    }
  }
}




