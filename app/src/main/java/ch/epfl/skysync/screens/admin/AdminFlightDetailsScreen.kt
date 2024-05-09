package ch.epfl.skysync.screens.admin

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.ConfirmFlightDetailUi
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.navigation.AdminBottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.components.FlightDetailBottom
import ch.epfl.skysync.components.FlightDetailUi
import ch.epfl.skysync.viewmodel.FlightsViewModel

@Composable
fun AdminFlightDetailScreen(
    navController: NavHostController,
    flightId: String,
    viewModel: FlightsViewModel
) {

  val flight by viewModel.getFlight(flightId).collectAsStateWithLifecycle()
  Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = { AdminBottomBar(navController) }) {
      padding ->
    if (flight is ConfirmedFlight) {
      ConfirmFlightDetailUi(
          confirmedFlight = flight as ConfirmedFlight,
          backClick = { navController.popBackStack() },
          paddingValues = padding,
          okClick = { navController.navigate(Route.ADMIN_HOME) },
      )
    } else {
      FlightDetailUi(
          backClick = { navController.popBackStack() },
          deleteClick = {
            viewModel.deleteFlight(flightId)
            navController.navigate(Route.ADMIN_HOME)
          },
          editClick = { navController.navigate(Route.MODIFY_FLIGHT + "/${flightId}") },
          confirmClick = { navController.navigate(Route.CONFIRM_FLIGHT + "/${flightId}") },
          padding = padding,
          flight = flight,
          bottom = { deleteClick, editClick, confirmClick ->
            FlightDetailBottom(deleteClick, editClick, confirmClick)
          })
    }
  }
}

// @Composable
// @Preview
// fun FlightDetailScreenPreview() {
//  val navController = rememberNavController()
//  val viewModel = UserViewModel.createViewModel(firebaseUser = null)
//  FlightDetailScreen(navController = navController, flightId = "1", viewModel = viewModel)
// }
