package ch.epfl.skysync.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import ch.epfl.skysync.ui.theme.lightOrange

/**
 * Composable function representing a back button with an arrow icon and text.
 *
 * @param backClick Callback function to be invoked when the back button is clicked.
 */
@Composable
fun Backbutton(backClick: () -> Unit) {
  IconButton(onClick = backClick, modifier = Modifier.fillMaxWidth(0.2f).testTag("BackButton")) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = lightOrange)
      Text(text = "Back", color = lightOrange)
    }
  }
}
