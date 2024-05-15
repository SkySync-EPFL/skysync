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
import java.time.LocalTime
import java.util.Date

fun LazyListScope.baseReportFields(
    defaultPadding: Dp,
    beginTime: LocalTime,
    endTime: LocalTime,
    pauseDuration: Long,
    flight: FinishedFlight,
    onConfirm: (Vehicle, String) -> Unit,
    vehicleProblem: MutableMap<Vehicle, String>,
    comments: String,
    onBeginTimeChange: (LocalTime) -> Unit,
    onEndTimeChange: (LocalTime) -> Unit,
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
