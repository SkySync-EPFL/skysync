package ch.epfl.skysync.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.FlightColor
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.screens.flightDetail.ClickButton
import ch.epfl.skysync.ui.theme.lightOrange
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Composable function to display a confirmation screen for a planned flight.
 *
 * @param plannedFlight The planned flight for which the confirmation screen is displayed.
 */
@Composable
fun Confirmation(plannedFlight: PlannedFlight, confirmClick: (ConfirmedFlight) -> Unit) {
  val id: String = plannedFlight.id
  val nPassengers: Int = plannedFlight.nPassengers
  val flightType: FlightType = plannedFlight.flightType
  val teamRoles = plannedFlight.team.roles
  val balloon = plannedFlight.balloon
  val basket = plannedFlight.basket
  val date: LocalDate = plannedFlight.date
  val timeSlot = plannedFlight.timeSlot
  val vehicles: List<Vehicle> = plannedFlight.vehicles
  val meetupLocationPassenger = "Nancy"
  val remarks: List<String> = emptyList()
  val meetupTimeTeam = LocalTime.now()
  val departureTimeTeam = meetupTimeTeam.plusHours(1)
  val meetupTimePassenger = departureTimeTeam.plusHours(1)
  var location: String = meetupLocationPassenger

  var selectedTeamColor by remember { mutableStateOf(FlightColor.NO_COLOR) }
  var selectedTeamMeetupTime by remember { mutableStateOf<LocalTime?>(null) }
  var selectedDepartureTime by remember { mutableStateOf<LocalTime?>(null) }
  var selectedPassengersMeetupTime by remember { mutableStateOf<LocalTime?>(null) }
  var remarkList: List<String> = remarks

  val options =
      listOf(
          FlightColor.RED,
          FlightColor.BLUE,
          FlightColor.ORANGE,
          FlightColor.YELLOW,
          FlightColor.PINK,
          FlightColor.NO_COLOR)

  val fontSize = 17.sp
  var showDialog by remember { mutableStateOf(false) }

  if (showDialog) {
    ConfirmAlertDialog(
        onDismissRequest = { showDialog = false },
        onConfirmation = {
          showDialog = false
          confirmClick(
              plannedFlight.confirmFlight(
                  meetupTimeTeam = selectedTeamMeetupTime!!,
                  departureTimeTeam = selectedDepartureTime!!,
                  meetupTimePassenger = selectedPassengersMeetupTime!!,
                  meetupLocationPassenger = location,
                  remarks = remarkList,
                  color = selectedTeamColor))
        },
        dialogTitle = "Confirm Flight",
        dialogText = "Are you sure you want to confirm this flight ?",
    )
  }
  LazyColumn(Modifier.testTag("LazyList")) {
    item {
      Text(
          modifier = Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 25.dp),
          text = "Confirmation of Flight $id",
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center)
      Text(
          modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
          text = "Information to confirm",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center)
      elementShow("Passengers nb", "$nPassengers", fontSize)
      elementShow("Flight type", flightType.name, fontSize)
      teamRoles.withIndex().forEach() { (i, role) ->
        elementShow("Teammate" + " " + (i + 1).toString(), role.roleType.name, fontSize)
      }
      if (balloon != null) {
        elementShow("Balloon", balloon.name, fontSize)
      }
      if (basket != null) {
        elementShow("Basket", basket.name, fontSize)
      }
      elementShow(
          "Date",
          (date.dayOfMonth.toString() + " " + date.month.toString() + " $timeSlot").lowercase(),
          fontSize)
      vehicles.withIndex().forEach() { (i, vehicle) ->
        elementShow("Vehicle" + " " + (i + 1).toString(), vehicle.name, fontSize)
      }
      Text(
          modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 16.dp),
          text = "Information to enter",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center)

      Row() {
        Column(Modifier.fillMaxWidth(0.5f)) {
          Spacer(modifier = Modifier.height(5.dp))
          Text(
              modifier = Modifier.fillMaxWidth(),
              text = "Team Color",
              fontSize = fontSize,
              textAlign = TextAlign.Center)
        }
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              OptionSelector(
                  options = options, onOptionSelected = { option -> selectedTeamColor = option })
            }
      }

      Text(
          modifier = Modifier.fillMaxWidth(),
          text = "MeetUp time",
          fontSize = fontSize,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center)

      Column(
          modifier = Modifier.padding(2.dp),
      ) {
        TimePicker({ time -> selectedTeamMeetupTime = time }, LocalTime.now(), 65.dp, "MeetUp")

        selectedTeamMeetupTime?.let { time ->
          Text(
              text = "Selected Time: ${time.format(DateTimeFormatter.ofPattern("HH:mm"))}",
              fontSize = 16.sp)
        }
      }
      // departure Time Team :
      Text(
          modifier = Modifier.fillMaxWidth(),
          text = "Departure Time (For Team)",
          fontSize = fontSize,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center)

      Column(
          modifier = Modifier.padding(2.dp),
      ) {
        TimePicker(
            { time -> selectedDepartureTime = time }, meetupTimePassenger, 65.dp, "Departure")

        selectedDepartureTime?.let { time ->
          Text(
              text = "Selected Time: ${time.format(DateTimeFormatter.ofPattern("HH:mm"))}",
              fontSize = 16.sp)
        }
      }
      // meetUp Time for passengers :
      Text(
          modifier = Modifier.fillMaxWidth(),
          text = "MeetUp (passengers)",
          fontSize = fontSize,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center)

      Column(
          modifier = Modifier.padding(2.dp),
      ) {
        TimePicker(
            { time -> selectedPassengersMeetupTime = time }, meetupTimeTeam, 65.dp, "MeetUp pass.")

        selectedPassengersMeetupTime?.let { time ->
          Text(
              text = "Selected Time: ${time.format(DateTimeFormatter.ofPattern("HH:mm"))}",
              fontSize = 16.sp)
        }
      }
      // meetUp location:
      val keyboardController = LocalSoftwareKeyboardController.current
      Column(
          modifier = Modifier.padding(2.dp),
      ) {
        AddElementComposable(
            title = "MeetUp Location",
            remarksList = { -> listOf(location) },
            onAddRemark = { remark ->
              location = remark
              (keyboardController)?.hide()
            })
      }

      Column(
          modifier = Modifier.padding(2.dp),
      ) {
        AddElementComposable(
            remarksList = { -> remarkList },
            onAddRemark = { remark ->
              if (remark != "") {
                remarkList += (remark)
              }
            })
      }
      if (selectedTeamMeetupTime != null &&
          selectedDepartureTime != null &&
          selectedPassengersMeetupTime != null) {
        Box(modifier = Modifier.fillMaxWidth().padding(2.dp), contentAlignment = Alignment.Center) {
          ClickButton(
              text = "Confirm",
              onClick = { showDialog = true },
              modifier = Modifier.fillMaxWidth(0.7f).testTag("ConfirmThisFlightButton"),
              color = Color.Green)
        }
      }
    }
  }
}
/**
 * Composable function to display two text elements side by side.
 *
 * @param left The text to display on the left side.
 * @param right The text to display on the right side.
 * @param fontSize The font size of the text elements.
 */
@Composable
fun elementShow(left: String, right: String, fontSize: TextUnit) {
  Row(Modifier.padding(top = 7.dp, bottom = 7.dp)) {
    Text(
        text = left,
        modifier = Modifier.fillMaxWidth(0.5f),
        fontSize = fontSize,
        textAlign = TextAlign.Center)
    Text(
        text = right,
        modifier = Modifier.fillMaxWidth(),
        fontSize = fontSize,
        textAlign = TextAlign.Center)
  }
}
/**
 * Composable function to create a time picker for selecting hours and minutes.
 *
 * @param onTimeSelected A lambda function to handle when a time is selected. Accepts a LocalTime
 *   parameter representing the selected time.
 * @param baseTime The initial time displayed in the time picker.
 * @param height The height of the time picker.
 * @param testTag A tag used for testing purposes.
 */
@Composable
fun TimePicker(
    onTimeSelected: (LocalTime) -> Unit,
    baseTime: LocalTime,
    height: Dp,
    testTag: String
) {
  val startHour = baseTime.hour
  val startMinute = baseTime.minute
  var hours by remember { mutableIntStateOf(startHour) }
  var minutes by remember { mutableIntStateOf(startMinute) }
  val keyboardController = LocalSoftwareKeyboardController.current

  Column(modifier = Modifier.padding(8.dp).height(height), horizontalAlignment = Alignment.Start) {
    Row(horizontalArrangement = Arrangement.Start) {
      OutlinedTextField(
          modifier = Modifier.fillMaxWidth(0.3f).testTag(testTag + "/Hours"),
          value =
              if (hours == -1) {
                ""
              } else {
                hours.toString()
              },
          onValueChange = { hours = it.toIntOrNull() ?: -1 },
          label = { Text("Hours") },
          keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
          keyboardActions =
              KeyboardActions(
                  onNext = {
                    // Move focus to the next TextField or perform any action
                  }))

      Spacer(modifier = Modifier.width(16.dp))

      OutlinedTextField(
          modifier = Modifier.fillMaxWidth(0.45f).testTag(testTag + "/Minutes"),
          value =
              if (minutes == -1) {
                ""
              } else {
                minutes.toString()
              },
          onValueChange = { minutes = it.toIntOrNull() ?: -1 },
          label = { Text("Minutes") },
          keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
          keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }))
      Spacer(modifier = Modifier.width(16.dp))
      Button(
          modifier = Modifier.fillMaxWidth(1f).testTag(testTag + "/SetTime"),
          colors = ButtonDefaults.buttonColors(backgroundColor = lightOrange),
          onClick = {
            if (hours !in 0..23 || minutes !in 0..59) {
              return@Button
            }
            if (minutes !in 0..59) {
              return@Button
            }
            val time = LocalTime.of(hours, minutes)
            keyboardController?.hide()
            onTimeSelected(time)
          },
      ) {
        Text(text = "Set Time")
      }
    }
  }
}
/**
 * Composable function to create a dropdown menu for selecting an option from a list.
 *
 * @param options The list of options to display in the dropdown menu.
 * @param onOptionSelected A lambda function to handle when an option is selected. Accepts a string
 *   parameter representing the selected option.
 */
@Composable
fun OptionSelector(
    options: List<FlightColor>,
    onOptionSelected: (FlightColor) -> Unit,
) {
  var expanded by remember { mutableStateOf(false) }
  var selected by remember { mutableStateOf("Select Color") }
  Column {
    OutlinedButton(onClick = { expanded = true }) { Text(text = selected) }

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      options.forEach { option ->
        DropdownMenuItem(
            onClick = {
              selected = option.toString()
              expanded = false
              onOptionSelected(option)
            },
            modifier = Modifier.padding(vertical = 1.dp).testTag(option.toString())) {
              Text(text = option.toString())
            }
      }
    }
  }
}
/**
 * Composable function to create an element with a text input field, a button to add the input as a
 * remark, and a list of remarks.
 *
 * @param title The title of the element. Defaults to "Remark".
 * @param remarksList A lambda function that provides the list of remarks.
 * @param onAddRemark A lambda function to handle adding a remark. Accepts a string parameter
 *   representing the remark to add.
 */
@Composable
fun AddElementComposable(
    title: String = "Remark",
    remarksList: () -> List<String>,
    onAddRemark: (String) -> Unit
) {
  var remarkText by remember { mutableStateOf("") }

  Column(
      modifier = Modifier.padding(0.dp).fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            value = remarkText,
            onValueChange = { remarkText = it },
            label = { Text("Enter $title") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions =
                KeyboardActions(
                    onDone = {
                      onAddRemark(remarkText)
                      remarkText = ""
                    }),
            modifier = Modifier.fillMaxWidth().padding(bottom = 0.dp))

        Button(
            onClick = {
              onAddRemark(remarkText)
              remarkText = ""
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = lightOrange)) {
              Text("Add $title")
            }

        Column {
          remarksList().forEach { remark ->
            if (remark != "") {
              Text(text = remark)
            }
          }
        }
      }
}
