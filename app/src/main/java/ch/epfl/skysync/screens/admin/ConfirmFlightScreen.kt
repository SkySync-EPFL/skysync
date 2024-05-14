package ch.epfl.skysync.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.components.ConfirmAlertDialog
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.components.FlightTeamMembersDetails
import ch.epfl.skysync.components.GlobalFlightMetricsDetails
import ch.epfl.skysync.components.LargeTitle
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.components.SmallTitle
import ch.epfl.skysync.components.SnackbarManager
import ch.epfl.skysync.components.forms.TimePickerButton
import ch.epfl.skysync.components.forms.TitledInputTextField
import ch.epfl.skysync.database.DateUtility
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.FlightColor
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.flight.flightColorOptions
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.*
import ch.epfl.skysync.viewmodel.FlightsViewModel
import java.time.LocalDate
import java.util.Date

/**
 * Represents a screen for confirming a flight.
 *
 * @param navController The navigation controller responsible for managing navigation within the
 *   app.
 * @param flightId The unique identifier of the flight to be confirmed.
 * @param viewModel The view model responsible for providing flight data and handling confirmation
 *   logic.
 */
@Composable
fun ConfirmationScreen(
    navController: NavHostController,
    flightId: String,
    viewModel: FlightsViewModel
) {
  val flight by viewModel.getFlight(flightId).collectAsStateWithLifecycle()
  if (flight == null) {
    LoadingComponent(isLoading = true, onRefresh = {}) {}
  } else {
    if (flight !is PlannedFlight) {
      navController.navigate(Route.ADMIN_HOME)
      SnackbarManager.showMessage("This action is not possible on this type of flight")
      return
    }

    val plannedFlight = flight as PlannedFlight
    if (!plannedFlight.readyToBeConfirmed()) {
      navController.navigate(Route.ADMIN_HOME)
      SnackbarManager.showMessage("Flight cannot be confirmed")
      return
    }

    Confirmation(navController = navController, plannedFlight = plannedFlight) {
      viewModel.addConfirmedFlight(it)
      navController.navigate(Route.ADMIN_HOME)
    }
  }
}

/**
 * Represents a screen to provide the latest information to confirm a planned flight.
 *
 * @param navController The navigation controller responsible for managing navigation within the
 *   app.
 * @param plannedFlight The planned flight to be confirmed.
 * @param onConfirm Callback function triggered when the flight confirmation is confirmed.
 */
@Composable
fun Confirmation(
    navController: NavHostController,
    plannedFlight: PlannedFlight,
    onConfirm: (ConfirmedFlight) -> Unit
) {
  val defaultPadding = 16.dp

  var selectedTeamColor by remember { mutableStateOf(FlightColor.NO_COLOR) }
  val showConfirmDialog = remember { mutableStateOf(false) }

  val remarks = remember { mutableStateListOf<String>() }

  val teamMeetUpTime = remember { mutableStateOf<Date?>(null) }
  val teamDepartureTime = remember { mutableStateOf<Date?>(null) }
  val passengerMeetupTime = remember { mutableStateOf<Date?>(null) }
  val meetupLocation = remember { mutableStateOf("") }

  val canConfirm =
      (teamMeetUpTime.value != null &&
          teamDepartureTime.value != null &&
          passengerMeetupTime.value != null &&
          meetupLocation.value != "")

  val times =
      mapOf(
          "Team meet up time" to teamMeetUpTime,
          "Team departure time" to teamDepartureTime,
          "Passengers meet up time" to passengerMeetupTime)

  val cardColor = Color.White
  Scaffold(
      topBar = { CustomTopAppBar(navController = navController, title = "Flight Confirmation") },
      bottomBar = { BottomBarConfirmButton(showConfirmDialog, canConfirm, defaultPadding) },
      containerColor = lightGray,
  ) { padding ->
    Column(modifier = Modifier.padding(padding)) {
      if (showConfirmDialog.value) {
        ConfirmAlertDialog(
            onDismissRequest = { showConfirmDialog.value = false },
            onConfirmation = {
              showConfirmDialog.value = false
              val confirmedFlight =
                  plannedFlight.confirmFlight(
                      meetupTimeTeam = DateUtility.dateToLocalTime(teamDepartureTime.value!!),
                      departureTimeTeam = DateUtility.dateToLocalTime(teamDepartureTime.value!!),
                      meetupTimePassenger =
                          DateUtility.dateToLocalTime(passengerMeetupTime.value!!),
                      meetupLocationPassenger = meetupLocation.value,
                      remarks = remarks,
                      color = selectedTeamColor)
              onConfirm(confirmedFlight)
              navController.navigate(Route.ADMIN_HOME)
            },
            dialogTitle = "Confirm Flight",
            dialogText = "Are you sure you want to confirm this flight ?",
        )
      }
      LazyColumn(
          modifier = Modifier.padding(8.dp).testTag("ConfirmationScreenLazyColumn"),
          verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        item {
          Card(colors = CardDefaults.cardColors(cardColor)) {
            GlobalFlightMetricsDetails(flight = plannedFlight, cardColor = cardColor)
          }
        }
        item {
          Card(colors = CardDefaults.cardColors(cardColor)) {
            FlightTeamMembersDetails(
                flight = plannedFlight, padding = defaultPadding, cardColor = cardColor) {
                  ColorOptionSelector(
                      defaultColor = flightColorOptions[FlightColor.NO_COLOR] as Color,
                      options = flightColorOptions,
                      onOptionSelected = { option -> selectedTeamColor = option })
                }
          }
        }
        items(items = times.keys.toList()) { title ->
          Card(colors = CardDefaults.cardColors(cardColor)) {
            TimePickerButton(
                title = title,
                padding = defaultPadding,
                buttonColor = lightOrange,
                setTime = { times[title]?.value = it })
          }
        }

        item {
          val keyboardController = LocalSoftwareKeyboardController.current
          Card(colors = CardDefaults.cardColors(cardColor)) {
            TitledInputTextField(
                padding = defaultPadding,
                title = "Meet up Location",
                value = meetupLocation.value,
                onValueChange = { meetupLocation.value = it },
                keyboardOptions =
                    KeyboardOptions.Default.copy(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }))
          }
        }
        item {
          Card(colors = CardDefaults.cardColors(cardColor)) {
            AddRemark(padding = defaultPadding, remarks = remarks)
            DisplayEditableRemarks(remarks = remarks, defaultPadding)
          }
        }
      }
    }
  }
}

/**
 * Displays a list of remarks along with delete buttons for each remark.
 *
 * @param remarks The list of remarks to be displayed.
 * @param padding The padding value to be applied to each remark row.
 */
@Composable
fun DisplayEditableRemarks(remarks: MutableList<String>, padding: Dp) {
  remarks.forEachIndexed { i, r ->
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
          Text(modifier = Modifier.padding(padding).weight(1f), text = r)
          IconButton(
              modifier = Modifier.testTag("DeleteRemark$r"), onClick = { remarks.removeAt(i) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Icon to delete Remark")
              }
        }
  }
}

/**
 * Displays a confirm button in the bottom app bar.
 *
 * @param showDialog A mutable state indicating whether to show the confirmation dialog.
 * @param canConfirm A boolean value indicating whether the confirmation button can be enabled.
 */
@Composable
fun BottomBarConfirmButton(showDialog: MutableState<Boolean>, canConfirm: Boolean, padding: Dp) {
  BottomAppBar {
    Button(
        modifier = Modifier.fillMaxSize().padding(padding),
        onClick = { showDialog.value = true },
        enabled = canConfirm) {
          SmallTitle(title = "Confirm", padding = 0.dp, color = Color.White)
        }
  }
}

/**
 * Displays a section for adding remarks.
 *
 * @param padding The padding value to be applied to the section.
 * @param remarks The list of remarks to which new remarks will be added.
 */
@Composable
fun AddRemark(padding: Dp, remarks: MutableList<String>) {
  var showAddMemberDialog by remember { mutableStateOf(false) }
  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically) {
        LargeTitle(title = "Remarks", padding = padding, color = Color.Black)
        TextButton(onClick = { showAddMemberDialog = true }) {
          SmallTitle(title = "Add remark", padding = 4.dp, color = Color.Unspecified)
          Icon(Icons.Default.Add, contentDescription = "Add Crew Member")
        }
      }
  AddRemarkAlertDialog(
      showAddMemberDialog = showAddMemberDialog,
      onDismiss = { showAddMemberDialog = false },
      padding = padding,
      onConfirm = { remark ->
        if (remark != "") {
          remarks.add(remark)
        }
        showAddMemberDialog = false
      })
}

@Composable
fun AddRemarkAlertDialog(
    showAddMemberDialog: Boolean,
    onDismiss: () -> Unit,
    padding: Dp,
    onConfirm: (String) -> Unit,
) {
  if (!showAddMemberDialog) {
    return
  }
  var remark by remember { mutableStateOf("") }
  AlertDialog(
      onDismissRequest = { onDismiss() },
      title = { Text(text = "New remark") },
      text = {
        TitledInputTextField(
            padding = padding, title = "", value = remark, onValueChange = { remark = it })
      },
      confirmButton = {
        TextButton(
            onClick = { onConfirm(remark) }, modifier = Modifier.testTag("AlertDialogConfirm")) {
              Text("Confirm", fontSize = 16.sp)
            }
      },
      dismissButton = {
        TextButton(onClick = { onDismiss() }, modifier = Modifier.testTag("AlertDialogDismiss")) {
          Text("Dismiss", fontSize = 16.sp)
        }
      })
}

/**
 * Displays a dropdown menu for selecting a color option.
 *
 * @param defaultColor The default color option.
 * @param options A map containing the available color options.
 * @param onOptionSelected Callback function triggered when a color option is selected.
 */
@Composable
fun ColorOptionSelector(
    defaultColor: Color,
    options: Map<FlightColor, Color>,
    onOptionSelected: (FlightColor) -> Unit
) {
  var expanded by remember { mutableStateOf(false) }
  var selectedColor by remember { mutableStateOf(defaultColor) }
  var selected by remember { mutableStateOf("Select team color") }

  Column {
    Button(
        modifier = Modifier.padding(16.dp).widthIn(min = 160.dp).heightIn(max = 35.dp),
        onClick = { expanded = true },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = selectedColor)) {
          Text(text = selected)
        }

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      options.forEach { (option, color) ->
        DropdownMenuItem(
            text = { Text(text = option.toString()) },
            onClick = {
              selectedColor = color
              expanded = false
              selected = option.toString()
              onOptionSelected(option)
            })
      }
    }
  }
}

@Preview
@Composable
fun ConfirmedFlightPreview() {
  val dummy =
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
  val navController = rememberNavController()
  Confirmation(navController, dummy) {}
}
