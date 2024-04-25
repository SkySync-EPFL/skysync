package ch.epfl.skysync.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class Side {
  LEFT,
  RIGHT,
}

/**
 * Composable function to display a switch button
 *
 * @param currentSide The currently selected side
 * @param textLeft The text displayed on the left side
 * @param textRight The text displayed on the right side
 * @param onClickLeft Callback called when clicking on the left side
 * @param onClickRight Callback called when clicking on the right side
 */
@Composable
fun SwitchButton(
    currentSide: Side,
    padding: PaddingValues,
    textLeft: String,
    textRight: String,
    onClickLeft: () -> Unit,
    onClickRight: () -> Unit
) {
  var leftColor = Color.White
  var leftTextColor = Color(0xFFFFA500)
  var rightTextColor = Color.Black
  var rightColor = Color.LightGray
  if (currentSide == Side.RIGHT) {
    leftColor = Color.LightGray
    leftTextColor = Color.Black
    rightColor = Color.White
    rightTextColor = Color(0xFFFFA500)
  }
  Box(
      modifier = Modifier.fillMaxSize(), // Adjust bottom padding as needed
      contentAlignment = Alignment.BottomCenter) {
        Row(
            modifier =
                Modifier.background(Color.LightGray, RoundedCornerShape(100.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(100.dp))
                    .testTag("SwitchButton"),
        ) {
          Button(
              onClick = onClickLeft,
              colors = ButtonDefaults.buttonColors(containerColor = leftColor),
              modifier =
                  Modifier.fillMaxWidth(0.5f).fillMaxHeight().testTag("SwitchButtonLeftButton"),
          ) {
            Text(
                text = textLeft,
                fontSize = 12.sp,
                color = leftTextColor,
                overflow = TextOverflow.Clip)
          }
          Button(
              onClick = onClickRight,
              colors = ButtonDefaults.buttonColors(containerColor = rightColor),
              modifier =
                  Modifier.fillMaxWidth(1f).fillMaxHeight().testTag("SwitchButtonRightButton"),
          ) {
            Text(
                text = textRight,
                fontSize = 12.sp,
                color = rightTextColor,
                overflow = TextOverflow.Clip,
                maxLines = 1)
          }
        }
      }
}

@Composable
fun SwitchButtonBetter(
    currentSide: Side,
    textLeft: String,
    textRight: String,
    onClickLeft: () -> Unit,
    onClickRight: () -> Unit
) {
  val darkOrange = Color(0xfffca503)
  val lightOrange = Color(0xfffad282)
  var leftColor = darkOrange
  val leftTextColor = Color.Black
  val rightTextColor = Color.Black
  var rightColor = lightOrange
  if (currentSide == Side.RIGHT) {
    leftColor = lightOrange
    rightColor = darkOrange
  }

  Row(
      modifier = Modifier.padding(15.dp).clip(RoundedCornerShape(15.dp)).testTag("SwitchButton"),
  ) {
    Button(
        onClick = onClickLeft,
        colors = ButtonDefaults.buttonColors(containerColor = leftColor),
        shape = RoundedCornerShape(0),
        modifier = Modifier.weight(1f).testTag("SwitchButtonLeftButton"),
    ) {
      Text(text = textLeft, fontSize = 12.sp, color = leftTextColor, overflow = TextOverflow.Clip)
    }
    Button(
        onClick = onClickRight,
        colors = ButtonDefaults.buttonColors(containerColor = rightColor),
        shape = RoundedCornerShape(0),
        modifier = Modifier.weight(1f).testTag("SwitchButtonRightButton"),
    ) {
      Text(
          text = textRight,
          fontSize = 12.sp,
          color = rightTextColor,
          overflow = TextOverflow.Clip,
          maxLines = 1)
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun tab() {
  val pageState = rememberPagerState(initialPage = 0) { 2 }
  var selectedTab by remember { mutableIntStateOf(pageState.currentPage) }
  Column() {
    TabRow(selectedTabIndex = selectedTab) {
      for (index in 0 until pageState.pageCount) {
        Tab(selected = index == selectedTab, onClick = { selectedTab = index }) {
          Text(text = "oui $index")
        }
      }
    }

    HorizontalPager(state = pageState) { p ->
      println("PATATE")
      println(p.toString())
      hello(p.toString())
    }
  }
}

@Composable
fun hello(s: String) {
  Text(text = "Hello ")
  Text(text = s)
}

@Preview
@Composable
fun tabPreview() {
  tab()
}
