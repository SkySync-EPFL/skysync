package ch.epfl.skysync.components.forms

import android.location.Location
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import ch.epfl.skysync.components.TimePickerDialog
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.util.getFormattedTime
import ch.epfl.skysync.util.inputValidation
import java.util.Date

@Composable
fun TimePickerField(defaultPadding: Dp, title: String, time: Date, setTime: (Date) -> Unit) {
    var showTimePicker by remember { mutableStateOf(false) }
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
        value = getFormattedTime(time),
        onValueChange = {},
        enabled = false,
        colors =
        OutlinedTextFieldDefaults.colors(
            // Make it look like it is enabled
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurface))
    if (showTimePicker) {
        TimePickerDialog(
            onCancel = { showTimePicker = false },
            onConfirm = {
                showTimePicker = false
                setTime(it.time)
            },
            modifier = Modifier.padding(defaultPadding))
    }
}

@Composable
fun LocationPickerField(
    location: Location,
    defaultPadding: Dp,
    title: String,
    setLocation: (Location) -> Unit
) {
    var active by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    Text(
        modifier = Modifier.fillMaxWidth().padding(horizontal = defaultPadding),
        text = title,
        style = MaterialTheme.typography.headlineSmall)
    SearchBarCustom(
        title = title,
        query = query,
        onQueryChange = { query = it },
        onSearch = { active = false },
        active = active,
        onActiveChange = { active = it },
        onElementClick = {
            setLocation(it)
            query = it.provider!!
        },
        propositions = emptyList<Location>(),
        showProposition = { it.provider.toString() })
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
                    CustomDropDownMenu(
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
                        modifier = Modifier.fillMaxWidth().testTag("Problem Field"),
                        text = "Problem",
                        style = MaterialTheme.typography.headlineSmall)
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = defaultPadding),
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
                        if (!inputValidation(problemNotChosenError, vehicleNotChosenError)) {
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
                    }) {
                    Text("Cancel")
                }
            })
    }
}

@Composable
fun PauseField(defaultPadding: Dp, pauseDuration: Long, setPauseDuration: (Long) -> Unit) {
    val title = "Pause duration"
    // TODO time range picker
    Text(
        modifier = Modifier.fillMaxWidth().padding(horizontal = defaultPadding).testTag(title),
        text = title,
        style = MaterialTheme.typography.headlineSmall)
    OutlinedTextField(
        modifier =
        Modifier.fillMaxWidth()
            .clickable { /* TODO */}
            .padding(start = defaultPadding, end = defaultPadding, bottom = defaultPadding)
            .testTag(title),
        value = "",
        onValueChange = {},
        enabled = false,
        colors =
        OutlinedTextFieldDefaults.colors(
            // Make it look like it is enabled
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurface))
}