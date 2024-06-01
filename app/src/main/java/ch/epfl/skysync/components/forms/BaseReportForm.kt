package ch.epfl.skysync.components.forms

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.flight.Vehicle
import java.util.Date

/**
 * Function to create the basic fields for a report form.
 *
 * @param defaultPadding The default padding for the fields.
 * @param beginTime The start time of the flight.
 * @param endTime The end time of the flight.
 * @param pauseDuration The duration of the pause.
 * @param flight The finished flight.
 * @param onConfirm Callback function when a vehicle problem is confirmed.
 * @param vehicleProblem A map of vehicles and their problems.
 * @param comments Any comments about the flight.
 * @param onBeginTimeChange Callback function when the begin time changes.
 * @param onEndTimeChange Callback function when the end time changes.
 * @param onPauseDurationChange Callback function when the pause duration changes.
 * @param onCommentsChange Callback function when the comments change.
 */
fun LazyListScope.baseReportFields(
    defaultPadding: Dp,
    beginTime: Date,
    endTime: Date,
    pauseDuration: Long,
    flight: FinishedFlight,
    onConfirm: (Vehicle, String) -> Unit,
    vehicleProblem: MutableMap<Vehicle, String>,
    comments: String,
    onBeginTimeChange: (Date) -> Unit,
    onEndTimeChange: (Date) -> Unit,
    onPauseDurationChange: (Long) -> Unit,
    onCommentsChange: (String) -> Unit
) {
  item {
    val beginTimeTitle = "Effective time of start"
    TimePickerField(defaultPadding, beginTimeTitle, beginTime) { onBeginTimeChange(it) }
  }

  item {
    val endTimeTitle = "Effective time of end"
    TimePickerField(defaultPadding, endTimeTitle, endTime) { onEndTimeChange(it) }
  }

  item { PauseField(defaultPadding, pauseDuration) { onPauseDurationChange(it) } }

  item { VehicleProblemField(defaultPadding, flight.vehicles, onConfirm = onConfirm) }
  items(vehicleProblem.keys.toList()) { vehicle ->
    Text(
        modifier = Modifier.fillMaxWidth().padding(horizontal = defaultPadding),
        text = vehicle.name,
        style = MaterialTheme.typography.headlineSmall)
    OutlinedTextField(
        value = vehicleProblem[vehicle] ?: "",
        onValueChange = { vehicleProblem[vehicle] = it },
        modifier = Modifier.fillMaxWidth().padding(horizontal = defaultPadding),
    )
  }

  item {
    TitledInputTextField(
        title = "Comments",
        value = comments,
        onValueChange = { onCommentsChange(it) },
        padding = defaultPadding)
  }
}

/**
 * Composable function to modify a vehicle problem.
 *
 * @param addProblem Boolean to indicate if a problem should be added.
 * @param vehicle The vehicle with the problem.
 * @param problem The problem of the vehicle.
 * @param modify Callback function to modify the vehicle problem.
 */
@Composable
fun ModifyVehicleProblem(
    addProblem: Boolean,
    vehicle: Vehicle?,
    problem: String,
    modify: (Vehicle, String) -> Unit
) {
  LaunchedEffect(addProblem) {
    if (addProblem) {
      modify(vehicle!!, problem)
    }
  }
}
