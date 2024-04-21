package ch.epfl.skysync.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.epfl.skysync.models.calendar.TimeSlot
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
    bottom: @Composable () -> Unit,
    tile: @Composable (date: LocalDate, time: TimeSlot) -> Unit
) {
  var currentWeekStartDate by remember { mutableStateOf(getStartOfWeek(LocalDate.now())) }
  Column(
      modifier = Modifier.fillMaxSize().padding(16.dp).background(Color.White),
  ) {
    WeekView(currentWeekStartDate, tile)
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

/**
 * Composable function to display a week view with a [tile] for each day and time slot.
 *
 * @param startOfWeek The start date of the week to be displayed.
 * @param tile The composable rendering each tile
 */
@Composable
fun WeekView(startOfWeek: LocalDate, tile: @Composable (date: LocalDate, time: TimeSlot) -> Unit) {
  val weekDays = (0..6).map { startOfWeek.plusDays(it.toLong()) }
  Column() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
          Spacer(modifier = Modifier.fillMaxWidth(0.01f))
          Box(contentAlignment = Alignment.Center) {
            Text(text = "AM", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
          }
          Box(contentAlignment = Alignment.Center) {
            Text(text = "PM", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
          }
          Spacer(modifier = Modifier.fillMaxWidth(0.01f))
        }

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