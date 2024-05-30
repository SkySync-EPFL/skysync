package ch.epfl.skysync.components.forms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.epfl.skysync.components.GenericTimePicker
import ch.epfl.skysync.components.RangeTimePicker
import ch.epfl.skysync.components.SmallTitle
import ch.epfl.skysync.database.DateUtility.dateToHourMinuteString
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.location.LocationPoint
import ch.epfl.skysync.util.hasError
import ch.epfl.skysync.viewmodel.FinishedFlightsViewModel
import java.util.Date

/**
 * Displays a field for picking a time.
 *
 * @param defaultPadding The default padding value to be applied to the field.
 * @param title The title or label for the time picker field.
 * @param time The current selected time.
 * @param setTime Callback function triggered when a new time is selected.
 */
@Composable
fun TimePickerField(defaultPadding: Dp, title: String, time: Date, setTime: (Date) -> Unit) {
  var showTimePicker by remember { mutableStateOf(false) }
  GenericTimePicker(
      padding = defaultPadding,
      showTimePicker = showTimePicker,
      onDismiss = { showTimePicker = false },
      onConfirm = {
        showTimePicker = false
        setTime(it)
      }) {}
  Text(
      modifier = Modifier.fillMaxWidth().padding(horizontal = defaultPadding),
      text = title,
      style = MaterialTheme.typography.headlineSmall)
  OutlinedTextField(
      modifier =
          Modifier.fillMaxWidth()
              .clickable { showTimePicker = true }
              .padding(start = defaultPadding, end = defaultPadding, bottom = defaultPadding)
              .testTag(title),
      value = dateToHourMinuteString(time),
      onValueChange = {},
      enabled = false,
      colors =
          OutlinedTextFieldDefaults.colors(
              // Make it look like it is enabled
              disabledTextColor = MaterialTheme.colorScheme.onSurface,
              disabledBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
              disabledPlaceholderColor = MaterialTheme.colorScheme.onSurface))
}

/**
 * A composable that displays a button for selecting a time using a time picker dialog.
 *
 * @param title The title displayed next to the button.
 * @param buttonColor The color of the button.
 * @param padding The padding applied around the button.
 * @param setTime Callback invoked when a time is selected. It provides a [Date] object containing
 *   the selected time.
 */
@Composable
fun TimePickerButton(title: String, buttonColor: Color, padding: Dp, setTime: (Date) -> Unit) {
  var showTimePicker by remember { mutableStateOf(false) }
  var time by remember { mutableStateOf<Date?>(null) }
  GenericTimePicker(
      padding = padding,
      showTimePicker = showTimePicker,
      onConfirm = {
        time = it
        showTimePicker = false
        setTime(it)
      },
      onDismiss = { showTimePicker = false }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
              SmallTitle(title = title, padding = padding, color = Color.Black)
              Button(
                  modifier =
                      Modifier.padding(horizontal = 8.dp)
                          .heightIn(max = 35.dp)
                          .testTag("TimePickerButton"),
                  onClick = { showTimePicker = true },
                  colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                  shape = RoundedCornerShape(10.dp)) {
                    Text(text = time?.let { dateToHourMinuteString(it) } ?: "Select Time")
                  }
            }
      }
}

@Composable
fun LocationPickerField(
    location: LocationPoint,
    defaultPadding: Dp,
    title: String,
    viewModel: FinishedFlightsViewModel,
    setLocation: (LocationPoint) -> Unit,
) {
  var active by remember { mutableStateOf(false) }
  var query by remember { mutableStateOf("") }
  val result by viewModel.searchResults.collectAsStateWithLifecycle()
  Text(
      modifier = Modifier.fillMaxWidth().padding(horizontal = defaultPadding),
      text = title,
      style = MaterialTheme.typography.headlineSmall)
  SearchBarCustom(
      title = title,
      query = query,
      onQueryChange = {
        query = it
        viewModel.getSearchLocation(it, location.time)
      },
      onSearch = { active = false },
      active = active,
      onActiveChange = { active = it },
      onElementClick = {
        setLocation(it)
        query = if (it.name == "") it.latlng().toString() else it.name
        active = false
      },
      propositions = result,
      showProposition = { if (it.name == "") it.latlng().toString() else it.name })
}

@Composable
fun VehicleProblemField(
    defaultPadding: Dp,
    vehicles: List<Vehicle>,
    onConfirm: (Vehicle, String) -> Unit,
) {
  var addNewProblem: String by remember { mutableStateOf("") }
  var problemNotChosenError: Boolean by remember { mutableStateOf(false) }
  var selectVehicle: Vehicle? by remember { mutableStateOf(null) }
  var vehicleNotChosenError: Boolean by remember { mutableStateOf(false) }
  var showAddProblemDialog by remember { mutableStateOf(false) }
  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = defaultPadding),
            text = "Vehicle Problems",
            style = MaterialTheme.typography.headlineSmall)
        IconButton(
            modifier = Modifier.padding(horizontal = defaultPadding).testTag("Add Problem Button"),
            onClick = { showAddProblemDialog = true }) {
              Icon(
                  modifier = Modifier,
                  imageVector = Icons.Default.Add,
                  contentDescription = "Add Problem")
            }
      }
  if (showAddProblemDialog) {
    AlertDialog(
        modifier = Modifier.testTag("User Dialog Field"),
        onDismissRequest = { showAddProblemDialog = false },
        title = { Text("Add New Member") },
        text = {
          Column {
            TitledDropDownMenu(
                defaultPadding = defaultPadding,
                title = "Vehicle",
                value = selectVehicle,
                onclickMenu = { item ->
                  selectVehicle = item
                  problemNotChosenError = false
                },
                items = vehicles,
                showString = { it?.name ?: "choose a vehicle" },
                isError = vehicleNotChosenError,
                messageError = "Please choose a vehicle")
            Text(
                modifier = Modifier.fillMaxWidth().padding(horizontal = defaultPadding),
                text = "Problem",
                style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(horizontal = defaultPadding)
                        .testTag("Problem Field"),
                value = addNewProblem,
                onValueChange = { addNewProblem = it },
                isError = problemNotChosenError,
                supportingText = {
                  if (problemNotChosenError) Text("Please enter the problem of the vehicle")
                },
            )
          }
        },
        confirmButton = {
          Button(
              modifier = Modifier.testTag("Add Vehicle Problem Button"),
              onClick = {
                showAddProblemDialog = false
                problemNotChosenError = addNewProblem == ""
                vehicleNotChosenError = selectVehicle == null
                if (!hasError(problemNotChosenError, vehicleNotChosenError)) {
                  onConfirm(selectVehicle!!, addNewProblem)
                  selectVehicle = null
                  addNewProblem = ""
                }
              }) {
                Text("Add")
              }
        },
        dismissButton = {
          Button(
              onClick = {
                showAddProblemDialog = false
                selectVehicle = null
                addNewProblem = ""
                problemNotChosenError = false
                vehicleNotChosenError = false
              }) {
                Text("Cancel")
              }
        })
  }
}

@Composable
fun PauseField(defaultPadding: Dp, pauseDuration: Long, setPauseDuration: (Long) -> Unit) {
  val title = "Pause duration"
  var showPausePicker by remember { mutableStateOf(false) }
  RangeTimePicker(
      padding = 16.dp,
      title = title,
      showDialog = showPausePicker,
      onDismiss = { showPausePicker = false },
      onConfirm = { setPauseDuration(it) })
  Text(
      modifier = Modifier.fillMaxWidth().padding(horizontal = defaultPadding),
      text = title,
      style = MaterialTheme.typography.headlineSmall)
  OutlinedTextField(
      modifier =
          Modifier.fillMaxWidth()
              .clickable { showPausePicker = true }
              .padding(start = defaultPadding, end = defaultPadding, bottom = defaultPadding)
              .testTag(title),
      value = pauseDuration.toString(),
      onValueChange = {},
      enabled = false,
      colors =
          OutlinedTextFieldDefaults.colors(
              // Make it look like it is enabled
              disabledTextColor = MaterialTheme.colorScheme.onSurface,
              disabledBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
              disabledPlaceholderColor = MaterialTheme.colorScheme.onSurface))
}
