package ch.epfl.skysync.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Function to calculate the start date of the week for the given date.
 *
 * @param date The input LocalDate for which the start date of the week is to be calculated.
 * @return The start date of the week containing the input date.
 */
fun getStartOfWeek(date: LocalDate): LocalDate {
  return date.minusDays(date.dayOfWeek.value.toLong() - 1)
}

/**
 * Composable function to display a calendar with a week view
 *
 * @param bottom The composable rendered at the bottom of the calendar
 * @param tile The composable rendered for each tile
 */
@Composable
fun ModularCalendar(
    padding: PaddingValues,
    bottom: @Composable () -> Unit,
    tile: @Composable (date: LocalDate, time: TimeSlot) -> Unit
) {
  var currentWeekStartDate by remember { mutableStateOf(getStartOfWeek(LocalDate.now())) }
  Column(modifier = Modifier.background(Color.White).fillMaxSize()) {
    WeekView(false, currentWeekStartDate, tile)
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
          Spacer(modifier = Modifier.width(5.dp))
          Button(
              onClick = { currentWeekStartDate = currentWeekStartDate.minusWeeks(1) },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))) {
                Text(text = "Prev Week", color = Color.DarkGray)
              }
          Spacer(modifier = Modifier.width(20.dp))
          Button(
              onClick = { currentWeekStartDate = currentWeekStartDate.plusWeeks(1) },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))) {
                Text(text = "Next Week", color = Color.DarkGray)
              }
          Spacer(modifier = Modifier.width(5.dp))
        }
    bottom()
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModularCalendarNew(
    padding: PaddingValues,
    isDraft: Boolean,
    tile: @Composable (date: LocalDate, time: TimeSlot) -> Unit
) {

  val pagerState = rememberPagerState(initialPage = 26, pageCount = { 52 })
  var currentWeekStartDate by remember { mutableStateOf(getStartOfWeek(LocalDate.now())) }
  var previousPage by remember { mutableIntStateOf(pagerState.currentPage) }

  val calendarBackgroundColor = if (isDraft) Color.LightGray else Color.White
  Column(modifier = Modifier.background(Color.White).padding(padding)) {
    LaunchedEffect(pagerState) {
      // Collect from the a snapshotFlow reading the currentPage
      snapshotFlow { pagerState.currentPage }
          .collect { page ->
            currentWeekStartDate =
                if (page > previousPage) {
                  currentWeekStartDate.plusWeeks(1)
                } else {
                  currentWeekStartDate.minusWeeks(1)
                }
            previousPage = page
          }
    }
    HorizontalPager(state = pagerState) { page ->
      // Our page content
      WeekView(isDraft, currentWeekStartDate, tile)
    }
    // WeekView(currentWeekStartDate, tile)
    if (isDraft) {
      SaveCancelButton(onCancel = { println("Cancel") }) { println("Save") }
    }
  }
}

@Composable
fun SaveCancelButton(onCancel: () -> Unit, onSave: () -> Unit) {
  Row(
      modifier =
          Modifier.clip(RoundedCornerShape(15.dp))
              .clip(RoundedCornerShape(15.dp))
              .testTag("SwitchButton"),
  ) {
    Button(
        onClick = onCancel,
        colors = ButtonDefaults.buttonColors(containerColor = lightRed),
        shape = RoundedCornerShape(0),
        modifier = Modifier.weight(1f).testTag("SwitchButtonLeftButton"),
    ) {
      androidx.compose.material3.Text(
          text = "Cancel", fontSize = 12.sp, overflow = TextOverflow.Clip)
    }
    Button(
        onClick = onSave,
        colors = ButtonDefaults.buttonColors(containerColor = lightGreen),
        shape = RoundedCornerShape(0),
        modifier = Modifier.weight(1f).testTag("SwitchButtonRightButton"),
    ) {
      androidx.compose.material3.Text(
          text = "Save",
          fontSize = 12.sp,
          overflow = TextOverflow.Clip,
      )
    }
  }
}

/**
 * Composable function to display a week view with a [tile] for each day and time slot.
 *
 * @param startOfWeek The start date of the week to be displayed.
 * @param tile The composable rendering each tile
 */
@Composable
fun WeekView(
    isDraft: Boolean,
    startOfWeek: LocalDate,
    tile: @Composable (date: LocalDate, time: TimeSlot) -> Unit
) {
  val weekDays = (0..6).map { startOfWeek.plusDays(it.toLong()) }
  Column(horizontalAlignment = Alignment.End) {
    Row(
        modifier = Modifier.fillMaxWidth(0.7f).padding(8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically) {
          if (isDraft)
              Text(
                  text = "Draft",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
              )
          Text(
              text = "AM",
              modifier = Modifier.fillMaxWidth().weight(1f),
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = Color.Black,
              textAlign = TextAlign.Center)
          Text(
              text = "PM",
              modifier = Modifier.fillMaxWidth().weight(1f),
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = Color.Black,
              textAlign = TextAlign.Center)
        }

    /*
        LazyColumn {
          itemsIndexed(weekDays) { i, day ->
            val scale = (1f / 10 * 10 / (10 - i))
            Row(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(scale),
                verticalAlignment = Alignment.CenterVertically,
            ) {
              Column(
                  modifier = Modifier.fillMaxHeight().fillMaxWidth(0.3f).background(Color.White),
                  verticalArrangement = Arrangement.Center,
              ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center) {
                      Text(
                          text = day.format(DateTimeFormatter.ofPattern("EEE", Locale.getDefault())),
                          fontSize = 16.sp,
                          color = Color.Black,
                          modifier = Modifier.fillMaxWidth(),
                          textAlign = TextAlign.Center)
                    }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center) {
                      Text(
                          text = day.format(DateTimeFormatter.ofPattern("MM/dd", Locale.getDefault())),
                          fontSize = 16.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color.Black,
                          modifier = Modifier.fillMaxWidth(),
                          textAlign = TextAlign.Center)
                    }
              }
              Row(
                  modifier = Modifier.fillMaxSize().background(Color.LightGray),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween) {
                    tile(day, TimeSlot.AM)
                    tile(day, TimeSlot.PM)
                  }
            }
            Divider(color = Color.Black, thickness = 1.dp)
          }
        }
    */

    weekDays.withIndex().forEach { (i, day) ->
      val scale = (1f / 10 * 10 / (10 - i))
      Row(
          modifier = Modifier.fillMaxWidth().fillMaxHeight(scale),
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(
            modifier = Modifier.fillMaxHeight().fillMaxWidth(0.3f).background(Color.White),
            verticalArrangement = Arrangement.Center,
        ) {
          Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center) {
                Text(
                    text = day.format(DateTimeFormatter.ofPattern("EEE", Locale.getDefault())),
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center)
              }
          Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center) {
                Text(
                    text = day.format(DateTimeFormatter.ofPattern("MM/dd", Locale.getDefault())),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center)
              }
        }
        Row(
            modifier = Modifier.fillMaxSize().background(Color.LightGray),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
              tile(day, TimeSlot.AM)
              tile(day, TimeSlot.PM)
            }
      }
      Divider(color = Color.Black, thickness = 1.dp)
    }
  }
}

@Preview
@Composable
fun CalendarPreview() {
  Scaffold {
    ModularCalendarNew(it, true) { date, time ->
      println(date)
      println(time)
    }
  }
}
