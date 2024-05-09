package ch.epfl.skysync.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp

/**
 * Composable that appears to confirm an action
 *
 * @param onDismissRequest Callback called when the user dismisses the dialog, such as by tapping
 *   outside of it.
 * @param onConfirmation Callback called when the action has indeed to be performed
 * @param dialogTitle Title displayed on the Dialog screen
 * @param dialogText Text displayed on the Dialog screen
 */
@Composable
fun ConfirmAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
) {
  AlertDialog(
      modifier = Modifier.testTag("AlertDialog"),
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
