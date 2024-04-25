package ch.epfl.skysync.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Text
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
 * @param modifier The modifier
 * @param isDraft Indicates if the [AvailabilityCalendar] is being modified
 * @param tile The composable rendered for each tile
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModularCalendar(
    modifier: Modifier,
    isDraft: Boolean,
    tile: @Composable (date: LocalDate, time: TimeSlot) -> Unit
) {

  val pagerState = rememberPagerState(initialPage = 20, pageCount = { 52 })
  var currentWeekStartDate by remember { mutableStateOf(getStartOfWeek(LocalDate.now())) }
  var previousPage by remember { mutableIntStateOf(pagerState.currentPage) }
  var initLaunchEffect = true
  LaunchedEffect(pagerState) {
    // Collect from the a snapshotFlow reading the currentPage
    snapshotFlow { pagerState.currentPage }
        .collect { page ->
          if (initLaunchEffect) {
            initLaunchEffect = false
          } else {
            currentWeekStartDate =
                if (page > previousPage) {
                  currentWeekStartDate.plusWeeks(1)
                } else {
                  currentWeekStartDate.minusWeeks(1)
                }
            previousPage = page
          }
        }
  }
  HorizontalPager(state = pagerState, modifier = modifier.testTag("HorizontalPager")) {
    WeekView(isDraft, currentWeekStartDate, tile)
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
  Column {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically) {
          val textColor = if (isDraft) Color.Black else Color.White
          mapOf("Draft" to textColor, "AM" to Color.Black, "PM" to Color.Black).forEach {
              (text, color) ->
            Text(
                text = text,
                modifier = Modifier.fillMaxWidth().weight(1f),
                fontSize = 16.sp,
                color = color,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center)
          }
        }
    weekDays.forEach { day ->
      Row(
          modifier = Modifier.fillMaxSize().weight(2f),
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(
            modifier = Modifier.fillMaxHeight().fillMaxWidth(0.3f).background(Color.White),
            verticalArrangement = Arrangement.Center,
        ) {
          Text(
              text = day.format(DateTimeFormatter.ofPattern("EEE", Locale.getDefault())),
              fontSize = 16.sp,
              color = Color.Black,
              modifier = Modifier.fillMaxWidth(),
              textAlign = TextAlign.Center)

          Text(
              text = day.format(DateTimeFormatter.ofPattern("MM/dd", Locale.getDefault())),
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = Color.Black,
              modifier = Modifier.fillMaxWidth(),
              textAlign = TextAlign.Center)
        }
        tile(day, TimeSlot.AM)
        tile(day, TimeSlot.PM)
      }
      Divider(color = Color.Black, thickness = 1.dp)
    }
  }
}

@Preview
@Composable
fun CalendarPreview() {

  ModularCalendar(Modifier, true) { date, time ->
    println(date)
    println(time)
  }
}
