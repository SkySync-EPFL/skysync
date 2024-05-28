package ch.epfl.skysync.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.ConfirmAlertDialog
import ch.epfl.skysync.components.AdminConfirmedFlightDetailBottom
import ch.epfl.skysync.components.BottomButton
import ch.epfl.skysync.components.ConnectivityStatus
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.components.FinishedFlightDetailBottom
import ch.epfl.skysync.components.FlightDetails
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.leftCornerRounded
import ch.epfl.skysync.ui.theme.lightGray
import ch.epfl.skysync.ui.theme.lightOrange
import ch.epfl.skysync.ui.theme.rightCornerRounded
import ch.epfl.skysync.viewmodel.FinishedFlightsViewModel
import ch.epfl.skysync.viewmodel.FlightsViewModel
import ch.epfl.skysync.viewmodel.InFlightViewModel
import java.time.LocalDate

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
                FlightDetailBottom2(
                    editClick = { navController.navigate(Route.MODIFY_FLIGHT + "/${flightId}") },
                    confirmClick = { navController.navigate(Route.CONFIRM_FLIGHT + "/${flightId}") },
                    deleteClick = { showConfirmDialog = true })
            }
          }
          is ConfirmedFlight -> {
            AdminConfirmedFlightDetailBottom(
                okClick = { navController.popBackStack() },
                deleteClick = { showConfirmDialog = true }
            )
          }
          is FinishedFlight -> {
            FinishedFlightDetailBottom(
                reportClick = { navController.navigate(Route.REPORT + "/${flightId}") },
                flightTraceClick = {
                  inFlightViewModel.startDisplayFlightTrace(flight as FinishedFlight)
                  navController.navigate(Route.FLIGHT)
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
fun FlightDetailBottom2(
    editClick: () -> Unit,
    confirmClick: () -> Unit,
    deleteClick: () -> Unit, ){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        BottomButton(onClick = { deleteClick()}, title = "delete", modifier = Modifier.weight(1f))
        BottomButton(onClick = { editClick()}, title = "edit", modifier = Modifier.weight(1f))
        BottomButton(onClick = { confirmClick()}, title = "confirm", modifier = Modifier.weight(1f))
    }
}




@Composable
@Preview
fun FlightDetailScreenPreview() {
  val plannedFlight =
      PlannedFlight(
          "1234",
          3,
          FlightType.DISCOVERY,
          Team(listOf(Role(RoleType.CREW), Role(RoleType.CREW))),
          Balloon("Balloon Name", BalloonQualification.LARGE, "Ballon Name"),
          Basket("Basket Name", true, "1234"),
          LocalDate.now().plusDays(3),
          TimeSlot.PM,
          listOf(
              Vehicle("Peugeot 308", "1234"),
              Vehicle("Peugeot 308", "1234"),
              Vehicle("Peugeot 308", "1234")))

  Scaffold(
      topBar = { Text("Flight Details") },
      bottomBar = {
        Text(text = "Bottom Bar")
      },
      containerColor = lightGray) { padding ->
        FlightDetails(flight = plannedFlight, padding = padding)
      }
}
