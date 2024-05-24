package ch.epfl.skysync.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import ch.epfl.skysync.components.ConfirmedFlightDetailBottom
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
import ch.epfl.skysync.ui.theme.*
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
    finishedFlightsViewModel: FinishedFlightsViewModel
) {

  val uncompletedFlight by viewModel.getFlight(flightId).collectAsStateWithLifecycle()
  val finishedFlight by finishedFlightsViewModel.getFlight(flightId).collectAsStateWithLifecycle()
  val flight = if (uncompletedFlight != null) uncompletedFlight else finishedFlight
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
            FlightDetailBottom(
                editClick = { navController.navigate(Route.MODIFY_FLIGHT + "/${flightId}") },
                confirmClick = { navController.navigate(Route.CONFIRM_FLIGHT + "/${flightId}") },
                deleteClick = { showConfirmDialog = true })
          }
          is ConfirmedFlight -> {
            ConfirmedFlightDetailBottom(
                { navController.popBackStack() }, { showConfirmDialog = true }, true)
          }
          is FinishedFlight -> {
            FinishedFlightDetailBottom(
                reportClick = { navController.navigate(Route.REPORT + "/${flightId}") },
                flightTraceClick = {
                  inFlightViewModel.setCurrentFlight(flightId)
                  inFlightViewModel.startDisplayFlightTrace()
                  navController.navigate(Route.FLIGHT)
                })
          }
        }
      },
      containerColor = lightGray) { padding ->
        if (flight == null) {
          LoadingComponent(isLoading = true, onRefresh = {}) {}
        } else {
          FlightDetails(flight = flight, padding = padding) {}
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
  BottomAppBar(containerColor = lightGray) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
      Spacer(modifier = Modifier.fillMaxWidth(0.05f))
      Row(modifier = Modifier.fillMaxWidth(0.9f / 3 / (1 - 0.05f)).testTag("DeleteButton")) {
        TextButton(
            onClick = { deleteClick() },
            shape = leftCornerRounded,
            border = BorderStroke(1.dp, Color.Black)) {
              Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Delete",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal)
              }
            }
      }
      Row(
          modifier =
              Modifier.fillMaxWidth((0.9f) / 3 / (1 - 0.05f - (0.9f) / 3)).testTag("EditButton")) {
            TextButton(
                onClick = { editClick() },
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, Color.Black),
            ) {
              Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Edit",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal)
              }
            }
          }
      Row(
          modifier =
              Modifier.fillMaxWidth((0.9f) / 3 / (1f - 0.05f - 2 * (0.9f) / 3))
                  .testTag("ConfirmButton")) {
            TextButton(
                onClick = { confirmClick() },
                shape = rightCornerRounded,
                border = BorderStroke(1.dp, Color.Black)) {
                  Row(
                      horizontalArrangement = Arrangement.Center,
                      modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Confirm",
                            color = Color.Black,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal)
                      }
                }
          }
      Spacer(modifier = Modifier.fillMaxWidth())
    }
  }
}

/**
 * Composable function to create a custom clickable button.
 *
 * @param text The text to be displayed on the button.
 * @param onClick The lambda function to be executed when the button is clicked.
 * @param modifier The modifier for the button layout.
 * @param color The color for the button background.
 */
@Composable
fun ClickButton(text: String, onClick: () -> Unit, modifier: Modifier, color: Color) {
  Button(
      onClick = onClick,
      modifier = modifier,
      colors = ButtonDefaults.buttonColors(containerColor = color)) {
        Text(text = text, color = Color.Black, overflow = TextOverflow.Clip)
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
        FlightDetailBottom(editClick = {}, confirmClick = {}, deleteClick = {})
        Text(text = "Bottom Bar")
      },
      containerColor = lightGray) { padding ->
        FlightDetails(flight = plannedFlight, padding = padding) {}
      }
}
