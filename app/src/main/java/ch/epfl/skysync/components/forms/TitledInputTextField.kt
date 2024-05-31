package ch.epfl.skysync.components.forms

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp

/**
 * A composable function that displays a titled input text field.
 *
 * @param padding The padding value to be applied to the text field.
 * @param title The title of the text field.
 * @param value The current value of the text field.
 * @param onValueChange Callback function triggered when the value of the text field changes.
 * @param isError Boolean indicating if there is an error.
 * @param messageError The error message to be displayed if there is an error.
 * @param keyboardOptions The keyboard options for the text field.
 * @param keyboardActions The keyboard actions for the text field.
 */
@Composable
fun TitledInputTextField(
    padding: Dp,
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    messageError: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
  Text(
      modifier = Modifier.fillMaxWidth().padding(horizontal = padding),
      text = title,
      style = MaterialTheme.typography.headlineSmall)
  OutlinedTextField(
      value = value,
      modifier = Modifier.fillMaxWidth().padding(horizontal = padding).testTag(title),
      onValueChange = onValueChange,
      isError = isError,
      supportingText = { if (isError) Text(messageError) },
      singleLine = true,
      keyboardOptions = keyboardOptions,
      keyboardActions = keyboardActions)
}
