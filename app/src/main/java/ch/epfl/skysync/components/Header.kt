package ch.epfl.skysync.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp

/**
 * Composable function representing a header with a back button and title.
 *
 * @param BackClick Callback function to be invoked when the back button is clicked.
 * @param title Title text to be displayed in the header.
 */
@Composable
fun Header(backClick: () -> Unit, title: String) {
  Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Backbutton(backClick)
    Column(
        modifier = Modifier.fillMaxWidth(0.75f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
              text = title,
              color = Color.Black,
              fontSize = 30.sp,
              modifier = Modifier.testTag("HeaderTitle"))
        }
  }
}
