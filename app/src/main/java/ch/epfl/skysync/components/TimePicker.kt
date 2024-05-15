package ch.epfl.skysync.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import ch.epfl.skysync.R
import java.util.Calendar
import java.util.Date

/**
 * A generic time picker composable that wraps a content composable and displays a time picker
 * dialog when [showTimePicker] is set to true.
 *
 * @param padding The padding to apply around the content composable.
 * @param showTimePicker A boolean value indicating whether to show the time picker dialog.
 * @param onDismiss Callback invoked when the time picker dialog is dismissed.
 * @param onConfirm Callback invoked when the user confirms the selected time. It provides a [Date]
 *   object containing the selected time.
 * @param f The content composable to be wrapped by the time picker. It should include the UI
 *   elements triggering the time picker dialog.
 */
@Composable
fun GenericTimePicker(
    padding: Dp,
    showTimePicker: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Date) -> Unit,
    f: @Composable () -> Unit
) {
  var time by remember { mutableStateOf<Date?>(null) }
  f()
  if (showTimePicker) {
    TimePickerDialog(
        onCancel = { onDismiss() },
        onConfirm = {
          time = it.time
          onConfirm(it.time)
        },
        modifier = Modifier.padding(padding))
  }
}

/**
 * Source : https://stackoverflow.com/questions/75853449/timepickerdialog-in-jetpack-compose
 * Displays a time picker dialog allowing the user to select a time.
 *
 * @param onCancel Callback invoked when the dialog is cancelled.
 * @param onConfirm Callback invoked when the user confirms the selected time. It provides a
 *   [Calendar] object containing the selected time.
 * @param modifier Modifier for styling and layout customization.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onCancel: () -> Unit,
    onConfirm: (Calendar) -> Unit,
    modifier: Modifier = Modifier
) {

  val time = Calendar.getInstance()
  time.timeInMillis = System.currentTimeMillis()

  var mode: DisplayMode by remember { mutableStateOf(DisplayMode.Picker) }
  val timeState: TimePickerState =
      rememberTimePickerState(
          initialHour = time[Calendar.HOUR_OF_DAY],
          initialMinute = time[Calendar.MINUTE],
          is24Hour = true)

  fun onConfirmClicked() {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, timeState.hour)
    cal.set(Calendar.MINUTE, timeState.minute)
    cal.isLenient = false
    onConfirm(cal)
  }

  // TimePicker does not provide a default TimePickerDialog, so we use our own PickerDialog:
  // https://issuetracker.google.com/issues/288311426
  PickerDialog(
      modifier = modifier.testTag("Time Picker"),
      onDismissRequest = onCancel,
      title = { Text("Select hour") },
      buttons = {
        DisplayModeToggleButton(
            displayMode = mode,
            onDisplayModeChange = { mode = it },
        )
        Spacer(Modifier.weight(1f))
        TextButton(onClick = onCancel) { Text("Cancel") }
        TextButton(onClick = ::onConfirmClicked, modifier = Modifier.testTag("TimePickerConfirm")) {
          Text(text = "Confirm")
        }
      },
  ) {
    val contentModifier = Modifier.padding(horizontal = 24.dp)
    when (mode) {
      DisplayMode.Picker ->
          androidx.compose.material3.TimePicker(modifier = contentModifier, state = timeState)
      DisplayMode.Input -> TimeInput(modifier = contentModifier, state = timeState)
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DisplayModeToggleButton(
    displayMode: DisplayMode,
    onDisplayModeChange: (DisplayMode) -> Unit,
    modifier: Modifier = Modifier,
) {
  when (displayMode) {
    DisplayMode.Picker ->
        IconButton(
            modifier = modifier,
            onClick = { onDisplayModeChange(DisplayMode.Input) },
        ) {
          Icon(
              painter = painterResource(id = R.drawable.baseline_keyboard_24),
              contentDescription = "Switch keyboard input",
          )
        }
    DisplayMode.Input ->
        IconButton(
            modifier = modifier,
            onClick = { onDisplayModeChange(DisplayMode.Picker) },
        ) {
          Icon(
              painter = painterResource(id = R.drawable.baseline_schedule_24),
              contentDescription = "Switch time picker",
          )
        }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickerDialog(
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
    buttons: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
  AlertDialog(
      modifier = modifier.width(IntrinsicSize.Min).height(IntrinsicSize.Min),
      onDismissRequest = onDismissRequest,
      properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        tonalElevation = 6.dp,
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Title
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
              ProvideTextStyle(MaterialTheme.typography.labelLarge) {
                Box(
                    modifier =
                        Modifier.align(Alignment.Start)
                            .padding(horizontal = 24.dp)
                            .padding(top = 16.dp, bottom = 20.dp),
                ) {
                  title()
                }
              }
            }
        // Content
        CompositionLocalProvider(LocalContentColor provides AlertDialogDefaults.textContentColor) {
          content()
        }
        // Buttons
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
          ProvideTextStyle(MaterialTheme.typography.labelLarge) {
            // TODO This should wrap on small screens, but we can't use AlertDialogFlowRow as it is
            // no public
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp, end = 6.dp, start = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
              buttons()
            }
          }
        }
      }
    }
  }
}
