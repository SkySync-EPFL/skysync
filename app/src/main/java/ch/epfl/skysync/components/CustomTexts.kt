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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
      modifier = Modifier.fillMaxWidth().padding(horizontal = padding).testTag(title + value),
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

/**
 * A composable function that displays a title.
 *
 * @param modifier The modifier to be applied to the title.
 * @param title The text of the title.
 * @param padding The padding value to be applied to the title.
 * @param style The style to be applied to the title.
 * @param color The color of the title.
 */
@Composable
fun Title(
    modifier: Modifier = Modifier,
    title: String,
    padding: Dp,
    style: TextStyle,
    color: Color = Color.Black
) {
  Text(modifier = modifier.padding(padding), text = title, style = style, color = color)
}

/**
 * A composable function that displays a header title.
 *
 * @param modifier The modifier to be applied to the title.
 * @param title The text of the title.
 * @param padding The padding value to be applied to the title.
 * @param color The color of the title.
 */
@Composable
fun HeaderTitle(
    modifier: Modifier = Modifier,
    title: String,
    padding: Dp,
    color: Color = Color.Black
) {
  Title(
      modifier,
      title,
      padding,
      MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
      color = color)
}

/**
 * A composable function that displays a large title.
 *
 * @param modifier The modifier to be applied to the title.
 * @param title The text of the title.
 * @param padding The padding value to be applied to the title.
 * @param color The color of the title.
 */
@Composable
fun LargeTitle(
    modifier: Modifier = Modifier,
    title: String,
    padding: Dp,
    color: Color = Color.Black
) {
  Title(
      modifier = modifier,
      title = title,
      padding = padding,
      style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
      color = color)
}

/**
 * A composable function that displays a small title.
 *
 * @param modifier The modifier to be applied to the title.
 * @param title The text of the title.
 * @param padding The padding value to be applied to the title.
 * @param color The color of the title.
 */
@Composable
fun SmallTitle(
    modifier: Modifier = Modifier,
    title: String,
    padding: Dp,
    color: Color = Color.Black
) {
  Title(
      modifier = modifier,
      title = title,
      padding = padding,
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
      color = color)
}

/**
 * A composable function that displays a medium title.
 *
 * @param modifier The modifier to be applied to the title.
 * @param title The text of the title.
 * @param padding The padding value to be applied to the title.
 * @param color The color of the title.
 * @param bold Boolean indicating if the title should be bold.
 */
@Composable
fun MediumTitle(
    modifier: Modifier = Modifier,
    title: String,
    padding: Dp,
    color: Color = Color.Black,
    bold: Boolean = false
) {
  val style =
      if (bold) MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
      else MaterialTheme.typography.headlineSmall
  Title(modifier = modifier, title = title, padding = padding, style = style, color = color)
}
