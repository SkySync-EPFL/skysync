package ch.epfl.skysync.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.ConfirmAlertDialog
import ch.epfl.skysync.components.ConfirmedFlightDetailBottom
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.components.FlightDetails
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.*
import ch.epfl.skysync.viewmodel.FlightsViewModel
import java.time.LocalDate

@Composable
fun AdminFlightDetailScreen(
    navController: NavHostController,
    flightId: String,
    viewModel: FlightsViewModel
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
            FlightDetailBottom(
                editClick = { navController.navigate(Route.MODIFY_FLIGHT + "/${flightId}") },
                confirmClick = { navController.navigate(Route.CONFIRM_FLIGHT + "/${flightId}") },
                deleteClick = { showConfirmDialog = true })
          }
          is ConfirmedFlight -> {
            ConfirmedFlightDetailBottom() { navController.popBackStack() }
          }
        }
      },
      containerColor = lightGray) { padding ->
        if (flight == null) {
          LoadingComponent(isLoading = true, onRefresh = {}) {}
        } else {
          FlightDetails(flight = flight, padding = padding)
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
          ClickButton(
              text = "Delete",
              onClick = { deleteClick() },
              modifier = Modifier.fillMaxWidth(0.3f).testTag("DeleteButton"),
              color = lightRed)
          ClickButton(
              text = "Edit",
              onClick = { editClick() },
              modifier = Modifier.fillMaxWidth(3 / 7f).testTag("EditButton"),
              color = Color.Yellow)
          ClickButton(
              text = "Confirm",
              onClick = { confirmClick() },
              modifier = Modifier.fillMaxWidth(0.7f).testTag("ConfirmButton"),
              color = lightGreen)
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
        FlightDetails(flight = plannedFlight, padding = padding)
      }
}
