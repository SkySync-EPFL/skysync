package ch.epfl.skysync.screens.flightDetail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.navigation.BottomBar
import java.time.LocalDate

@Composable
fun FlightDetailScreen(
    navController: NavHostController,
    flightId: String,
    // viewModel: ViewModel
) {
  val fakeFlight =
      PlannedFlight(
          flightId,
          1,
          FlightType.FONDUE,
          Team(listOf()),
          null,
          null,
          LocalDate.now(),
          TimeSlot.AM,
          listOf())
  Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = { BottomBar(navController) }) { padding ->
    FlightDetailUi(
        backClick = { navController.popBackStack() },
        deleteClick = { navController.popBackStack() },
        editClick = {},
        confirmClick = {},
        padding = padding,
        flight = fakeFlight)
  }
}

// @Composable
// @Preview
// fun FlightDetailScreenPreview() {
//  val navController = rememberNavController()
//  val viewModel = UserViewModel.createViewModel(firebaseUser = null)
//  FlightDetailScreen(navController = navController, flightId = "1", viewModel = viewModel)
// }
