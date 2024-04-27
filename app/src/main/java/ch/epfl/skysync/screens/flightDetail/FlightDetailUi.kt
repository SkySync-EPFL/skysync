package ch.epfl.skysync.screens.flightDetail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.epfl.skysync.components.Header
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle

/**
 * Composable function that displays the UI for flight details. It consists of a header, body, and
 * bottom section with action buttons.
 *
 * @param backClick Callback function invoked when the back button is clicked.
 * @param deleteClick Callback function invoked when the delete button is clicked.
 * @param editClick Callback function invoked when the edit button is clicked.
 * @param confirmClick Callback function invoked when the confirm button is clicked.
 * @param padding PaddingValues to apply to the content.
 * @param flight The flight details to display.
 */
@Composable
fun FlightDetailUi(
    backClick: () -> Unit,
    deleteClick: () -> Unit,
    editClick: () -> Unit,
    confirmClick: () -> Unit,
    padding: PaddingValues,
    flight: Flight?,
) {
  var showDialog by remember { mutableStateOf(false) }

  if (showDialog) {
    AlertDialogExample(
        onDismissRequest = { showDialog = false },
        onConfirmation = {
          showDialog = false
          deleteClick()
        },
        dialogTitle = "Delete Flight",
        dialogText = "Are you sure you want to delete this flight ?",
        icon = Icons.Default.Info)
  }
  Column(
      modifier = Modifier.fillMaxSize().background(Color.White),
  ) {
    Header(backClick = backClick, title = "Flight Detail")

    Box(modifier = Modifier.fillMaxHeight().padding(padding)) {
      if (flight == null) {
        LoadingComponent(isLoading = true, onRefresh = {}) {}
      } else {
        FlightDetailBody(flight, padding)
      }
      FlightDetailBottom(
          editClick = editClick, confirmClick = confirmClick, deleteClick = { showDialog = true })
    }
  }
}
/**
 * Composable function that displays the body of the flight detail screen.
 *
 * @param flight The flight details to display.
 * @param padding PaddingValues to apply to the content.
 */
@Composable
fun FlightDetailBody(flight: Flight, padding: PaddingValues) {
  Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f).padding(padding)) {
    Spacer(modifier = Modifier.fillMaxHeight(0.05f))
    Row() {
      Column(
          modifier = Modifier.fillMaxWidth(0.7f),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = flight.flightType.name,
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold)
            Text(
                text = flight.nPassengers.toString() + " Pax",
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold)
          }
      Column(
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                flight.date.toString(),
                fontSize = 15.sp,
                color = Color.Black,
            )
            Text(
                flight.timeSlot.name,
                color = Color.Black,
                fontSize = 15.sp,
            )
          }
    }
    Row {
      Column(
          modifier = Modifier.fillMaxWidth(0.5f),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally) {
            TextBar(textLeft = "Balloon", textRight = flight.balloon?.name ?: "None")
          }
      Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally) {
            TextBar(textLeft = "Basket", textRight = flight.basket?.name ?: "None")
          }
    }
    ScrollableBoxWithButton("Team") { TeamRolesList(team = flight.team) }

    ScrollableBoxWithButton("Vehicles") { VehicleListText(vehicle = flight.vehicles) }
  }
}
/**
 * Composable function that displays the bottom section of the flight detail screen.
 *
 * @param deleteClick Callback function invoked when the delete button is clicked.
 * @param editClick Callback function invoked when the edit button is clicked.
 * @param confirmClick Callback function invoked when the confirm button is clicked.
 */
@Composable
fun FlightDetailBottom(
    deleteClick: () -> Unit,
    editClick: () -> Unit,
    confirmClick: () -> Unit,
) {
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
          ClickButton(
              text = "Delete",
              onClick = { deleteClick() },
              modifier = Modifier.fillMaxWidth(0.3f).testTag("DeleteButton"),
              color = Color.Red)
          ClickButton(
              text = "Edit",
              onClick = { editClick() },
              modifier = Modifier.fillMaxWidth(3 / 7f).testTag("EditButton"),
              color = Color.Yellow)
          ClickButton(
              text = "Confirm",
              onClick = { confirmClick() },
              modifier = Modifier.fillMaxWidth(0.7f).testTag("ConfirmButton"),
              color = Color.Green)
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
/**
 * Composable function that displays a row with two text elements separated by a divider.
 *
 * @param textLeft The text to display on the left side.
 * @param textRight The text to display on the right side.
 */
@Composable
fun TextBar(textLeft: String, textRight: String) {
  Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween) {
        Column(
            modifier = Modifier.fillMaxWidth(0.5f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = textLeft, color = Color.Black, fontSize = 15.sp)
            }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                  text = textRight,
                  fontSize = 15.sp,
                  color = Color.Black,
                  modifier = Modifier.testTag(textLeft + textRight))
            }
      }
  Spacer(modifier = Modifier.height(8.dp))
  Divider(color = Color.Black, thickness = 1.dp)
  Spacer(modifier = Modifier.height(8.dp))
}
/**
 * Composable function that displays a list of team roles.
 *
 * @param team The team containing the roles to display.
 */
@Composable
fun TeamRolesList(team: Team) {
  if (team.roles.isEmpty()) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "No team member", color = Color.Black)
        }
  } else {
    LazyColumn(modifier = Modifier.testTag("TeamList").fillMaxHeight(0.5f)) {
      itemsIndexed(team.roles) { index, role ->
        val firstname = role.assignedUser?.firstname ?: ""
        val lastname = role.assignedUser?.lastname ?: ""
        val name = "$firstname $lastname"
        TextBar(textLeft = "Member $index: ${role.roleType.name}", textRight = name)
      }
    }
  }
}
/**
 * Composable function that displays a list of vehicles.
 *
 * @param vehicle The list of vehicles to display.
 */
@Composable
fun VehicleListText(vehicle: List<Vehicle>) {
  if (vehicle.indices.isEmpty()) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
              text = "No vehicle",
              color = Color.Black,
          )
        }
  } else {
    LazyColumn(modifier = Modifier.testTag("VehicleList")) {
      itemsIndexed(vehicle) { index, car ->
        TextBar(textLeft = "Vehicle $index", textRight = car.name)
      }
    }
  }
}
/**
 * Composable function that displays a button that, when clicked, toggles the visibility of a
 * content area.
 *
 * @param name The text to display on the button.
 * @param content The content to be displayed when the button is clicked.
 */
@Composable
fun ScrollableBoxWithButton(name: String, content: @Composable () -> Unit) {
  var expanded by remember { mutableStateOf(false) }

  Column {
    Button(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
          Text(text = name, color = Color.White)
        }

    if (expanded) {
      content()
    }
  }
}

/**
 * Composable that appears when the delete button is clicked.
 *
 * @param onDismissRequest Callback called when the user dismisses the dialog, such as by tapping
 *   outside of it.
 * @param onConfirmation Callback called when the flight has to be deleted
 * @param dialogTitle Title displayed on the Dialog screen
 * @param dialogText Text displayed on the Dialog screen
 * @param icon Icon displayed on the Dialog screen
 */
@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
  AlertDialog(
      modifier = Modifier.testTag("AlertDialog"),
      icon = { Icon(icon, contentDescription = "Example Icon") },
      title = { Text(text = dialogTitle) },
      text = { Text(text = dialogText, fontSize = 16.sp) },
      onDismissRequest = { onDismissRequest() },
      confirmButton = {
        TextButton(
            onClick = { onConfirmation() }, modifier = Modifier.testTag("AlertDialogConfirm")) {
              Text("Confirm", fontSize = 16.sp)
            }
      },
      dismissButton = {
        TextButton(
            onClick = { onDismissRequest() }, modifier = Modifier.testTag("AlertDialogDismiss")) {
              Text("Dismiss", fontSize = 16.sp)
            }
      })
}
