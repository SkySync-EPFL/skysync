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
import androidx.compose.material3.Divider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.components.ConfirmAlertDialog
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.components.HeaderTitle
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
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.*
import ch.epfl.skysync.viewmodel.FlightsViewModel
import java.time.LocalDate
import java.util.Date

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
 * Composable function to display a confirmation screen for a planned flight.
 *
 * @param plannedFlight The planned flight for which the confirmation screen is displayed.
 */
@Composable
fun Confirmation(
    navController: NavHostController,
    plannedFlight: PlannedFlight,
    onConfirm: (ConfirmedFlight) -> Unit
) {
  val teamRoles = plannedFlight.team.roles
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

  val metrics =
      mapOf(
          "Day of flight" to DateUtility.localDateToString(plannedFlight.date),
          "Time slot" to plannedFlight.timeSlot.toString(),
          "Number of Passengers" to "${plannedFlight.nPassengers}",
          "Flight type" to plannedFlight.flightType.name,
          "Balloon" to (plannedFlight.balloon?.name ?: "Unset"),
          "Basket" to (plannedFlight.basket?.name ?: "Unset"))

  val flightColorOptions =
      mapOf(
          FlightColor.RED to lightRed,
          FlightColor.BLUE to blue,
          FlightColor.GREEN to lightGreen,
          FlightColor.PINK to lightPink,
          FlightColor.ORANGE to lightOrange,
          FlightColor.NO_COLOR to Color.Gray)

  Scaffold(
      topBar = { CustomTopAppBar(navController = navController, title = "Flight Confirmation") },
      bottomBar = { ConfirmButton(showConfirmDialog, canConfirm) },
      containerColor = Color.LightGray,
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
          Card(colors = CardDefaults.cardColors(Color.White)) {
            metrics.forEach { (metric, value) -> SingleMetric(metric = metric, value = value) }
            ListMetric(
                metric = "Vehicles",
                values = plannedFlight.vehicles.map { vehicle -> vehicle.name })
          }
        }
        item {
          Card(colors = CardDefaults.cardColors(Color.White)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              HeaderTitle(title = "Team", defaultPadding, color = Color.Black)
              OptionSelector(
                  defaultColor = flightColorOptions[FlightColor.NO_COLOR] as Color,
                  options = flightColorOptions,
                  onOptionSelected = { option -> selectedTeamColor = option })
            }
            teamRoles.forEach { role ->
              var values = listOf("Unset")
              if (role.assignedUser != null) {
                values = listOf(role.assignedUser!!.firstname, role.assignedUser!!.lastname)
              }
              ListMetric(metric = role.roleType.description, values = values)
            }
          }
        }

        items(items = times.keys.toList()) { title ->
          Card(colors = CardDefaults.cardColors(Color.White)) {
            TimePickerButton(
                title = title,
                padding = defaultPadding,
                buttonColor = lightOrange,
                setTime = { times[title]?.value = it })
          }
        }

        item {
          val keyboardController = LocalSoftwareKeyboardController.current
          Card(colors = CardDefaults.cardColors(Color.White)) {
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
          Card(colors = CardDefaults.cardColors(Color.White)) {
            AddRemark(padding = defaultPadding, remarks = remarks)
            DisplayRemarks(remarks = remarks, defaultPadding)
          }
        }
      }
    }
  }
}

@Composable
fun DisplayRemarks(remarks: MutableList<String>, padding: Dp) {
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

@Composable
fun ConfirmButton(showDialog: MutableState<Boolean>, canConfirm: Boolean) {
  BottomAppBar(modifier = Modifier.heightIn(max = 60.dp), containerColor = Color.LightGray) {
    Button(
        modifier = Modifier.fillMaxSize(),
        onClick = { showDialog.value = true },
        shape = RoundedCornerShape(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = lightGreen),
        enabled = canConfirm) {
          SmallTitle(title = "Confirm", padding = 0.dp, color = Color.Black)
        }
  }
}

@Composable
fun ListMetric(metric: String, values: List<String>) {
  Row(modifier = Modifier.padding(8.dp)) {
    Text(
        text = metric,
        modifier = Modifier.fillMaxWidth().weight(1f),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Left)
    Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
      values.forEach { value ->
        Text(
            text = value,
            modifier = Modifier.fillMaxWidth().padding(1.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center)
      }
    }
  }
  Divider(modifier = Modifier.padding(start = 20.dp), color = Color.LightGray, thickness = 1.dp)
}

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

@Composable
fun SingleMetric(metric: String, value: String) {
  ListMetric(metric, listOf(value))
}

@Composable
fun OptionSelector(
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
