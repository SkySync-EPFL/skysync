package ch.epfl.skysync.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.AdminConfirmedFlightDetailBottom
import ch.epfl.skysync.components.BottomButton
import ch.epfl.skysync.components.ConfirmAlertDialog
import ch.epfl.skysync.components.ConnectivityStatus
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.components.FinishedFlightDetailBottom
import ch.epfl.skysync.components.FlightDetails
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.lightGray
import ch.epfl.skysync.viewmodel.FlightsViewModel
import ch.epfl.skysync.viewmodel.InFlightViewModel

/**
 * Composable function that displays the admin flight detail screen.
 *
 * @param navController The navigation controller
 * @param flightId The flight id
 * @param viewModel The view model
 * @param inFlightViewModel The in flight view model
 * @param connectivityStatus The connectivity status
 */
@Composable
fun AdminFlightDetailScreen(
    navController: NavHostController,
    flightId: String,
    viewModel: FlightsViewModel,
    inFlightViewModel: InFlightViewModel,
    connectivityStatus: ConnectivityStatus
) {

  val flight by viewModel.getFlight(flightId).collectAsStateWithLifecycle()
  var showConfirmDialog by remember { mutableStateOf(false) }
  if (showConfirmDialog) {
    ConfirmAlertDialog(
        onDismissRequest = { showConfirmDialog = false },
        onConfirmation = {
          showConfirmDialog = false
          viewModel.deleteFlight(flightId)
          navController.navigate(Route.ADMIN_HOME)
        },
        dialogTitle = "Delete Flight",
        dialogText = "Are you sure you want to delete this flight ?",
    )
  }
  Scaffold(
      topBar = { CustomTopAppBar(navController = navController, title = "Flight Detail") },
      bottomBar = {
        when (flight) {
          is PlannedFlight -> {
            if (connectivityStatus.isOnline()) {
              FlightDetailBottom(
                  editClick = { navController.navigate(Route.MODIFY_FLIGHT + "/${flightId}") },
                  confirmClick = { navController.navigate(Route.CONFIRM_FLIGHT + "/${flightId}") },
                  deleteClick = { showConfirmDialog = true })
            }
          }
          is ConfirmedFlight -> {
            AdminConfirmedFlightDetailBottom(
                okClick = { navController.popBackStack() },
                deleteClick = { showConfirmDialog = true })
          }
          is FinishedFlight -> {
            FinishedFlightDetailBottom(
                reportClick = { navController.navigate(Route.REPORT + "/${flightId}") },
                flightTraceClick = {
                  inFlightViewModel.startDisplayFlightTrace(flight as FinishedFlight)
                  navController.navigate(Route.ADMIN_FLIGHT)
                })
          }
        }
      },
      containerColor = lightGray) { padding ->
        if (flight == null) {
          LoadingComponent(isLoading = true, onRefresh = {}) {}
        } else {
          FlightDetails(flight = flight!!, padding = padding)
        }
      }
}

/**
 * Composable function that displays the bottom section of the flight detail screen.
 *
 * @param editClick Callback function invoked when the edit button is clicked.
 * @param confirmClick Callback function invoked when the confirm button is clicked.
 * @param deleteClick Callback function invoked when the delete button is clicked.
 */
@Composable
fun FlightDetailBottom(
    editClick: () -> Unit,
    confirmClick: () -> Unit,
    deleteClick: () -> Unit,
) {
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
    BottomButton(onClick = { deleteClick() }, title = "delete", modifier = Modifier.weight(1f))
    BottomButton(onClick = { editClick() }, title = "edit", modifier = Modifier.weight(1f))
    BottomButton(onClick = { confirmClick() }, title = "confirm", modifier = Modifier.weight(1f))
  }
}
