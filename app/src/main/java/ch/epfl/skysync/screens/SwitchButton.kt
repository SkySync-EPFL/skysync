package ch.epfl.skysync.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SwitchButton(
    Availability: Boolean,
    padding: PaddingValues,
    onClick: () -> Unit,
    onClickRight: () -> Unit
) {
  var leftColor = Color.White
  var leftTextColor = Color(0xFFFFA500)
  var rightTextColor = Color.Black
  var rightColor = Color.LightGray
  if (Availability) {
    leftColor = Color.LightGray
    leftTextColor = Color.Black
    rightColor = Color.White
    rightTextColor = Color(0xFFFFA500)
  }
  Box(
      modifier =
          Modifier.fillMaxSize()
              .padding(
                  bottom = padding.calculateBottomPadding()), // Adjust bottom padding as needed
      contentAlignment = Alignment.BottomCenter) {
        Row(
            modifier =
                Modifier.background(Color.LightGray, RoundedCornerShape(100.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(100.dp)),
        ) {
          Button(
              onClick,
              colors = ButtonDefaults.buttonColors(containerColor = leftColor),
              modifier = Modifier.fillMaxWidth(0.5f).fillMaxHeight(),
          ) {
            Text(
                text = "Flight Calendar",
                fontSize = 12.sp,
                color = leftTextColor,
                modifier = Modifier,
                overflow = TextOverflow.Clip)
          }
          Button(
              onClickRight,
              colors = ButtonDefaults.buttonColors(containerColor = rightColor),
              modifier = Modifier.fillMaxWidth(1f).fillMaxHeight(),
          ) {
            Text(
                text = "Availability Calendar",
                fontSize = 12.sp,
                color = rightTextColor,
                overflow = TextOverflow.Clip,
                maxLines = 1)
          }
        }
      }
}
