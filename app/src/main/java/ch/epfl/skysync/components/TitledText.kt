package ch.epfl.skysync.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Composable function for displaying a titled text field.
 *
 * @param padding The padding to be applied to the text field.
 * @param title The title of the text field.
 * @param value The value to be displayed in the text field.
 */
@Composable
fun TitledText(
    padding: Dp,
    title: String,
    value: String,
) {

  Text(
      modifier = Modifier.fillMaxWidth().padding(horizontal = padding),
      text = title,
      style = MaterialTheme.typography.headlineSmall,
      color = Color.Black)
  Spacer(modifier = Modifier.padding(4.dp))
  Text(
      text = value,
      modifier = Modifier.fillMaxWidth().padding(horizontal = padding.plus(4.dp)).testTag(title),
      style = MaterialTheme.typography.bodyLarge,
  )
  Spacer(modifier = Modifier.padding(12.dp))
}
