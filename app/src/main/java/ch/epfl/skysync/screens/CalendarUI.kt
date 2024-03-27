package ch.epfl.skysync.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
/**
 * Composable function to display a calendar view.
 * @param modularShow a Composable function that what to display each tile in the calendar depending
 * on the day and if it's AM/PM (AM=0:00 and PM=12:00).
 */
@Composable
fun Calendar(modularShow: @Composable (LocalDateTime, Dp) -> Unit) {
  var currentWeekStartDate by remember { mutableStateOf(getStartOfWeek(LocalDate.now())) }

  Column(
      modifier = Modifier.fillMaxSize().padding(16.dp),
  ) {
    WeekView(currentWeekStartDate, modularShow)
    Spacer(modifier = Modifier.height(8.dp))
    Row {
      Button(onClick = { currentWeekStartDate = currentWeekStartDate.minusWeeks(1) }) {
        Text("Previous Week")
      }
      Spacer(modifier = Modifier.width(8.dp))
      Button(onClick = { currentWeekStartDate = currentWeekStartDate.plusWeeks(1) }) {
        Text("Next Week")
      }
    }
  }
}
/**
 * Composable function to display a week view with customizable tiles for each day.
 * @param startOfWeek the start date of the week.
 * @param modularShow a Composable function that what to display each tile in the calendar depending
 * on the day and if it's AM/PM (AM=0:00 and PM=12:00).
 */
@Composable
fun WeekView(startOfWeek: LocalDate, modularShow: @Composable (LocalDateTime, Dp) -> Unit) {
  val weekDays = (0..6).map { startOfWeek.plusDays(it.toLong()) }
  Column {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
          Spacer(modifier = Modifier.width(120.dp))
          Text(text = "AM", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
          Text(text = "PM", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    weekDays.forEach { day ->
      Row(
          modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = day.format(DateTimeFormatter.ofPattern("EEE, MM/dd", Locale.getDefault())),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.width(120.dp))
            modularShow(day.atStartOfDay(), 25.dp)
            modularShow(day.atTime(12, 0), 25.dp)
          }
    }
  }
}
/**
 * Function to calculate the start date of the week for the given date.
 * @param date The input LocalDate for which the start date of the week is to be calculated.
 * @return The start date of the week containing the input date.
 */
fun getStartOfWeek(date: LocalDate): LocalDate {
  return date.minusDays(date.dayOfWeek.value.toLong() - 1)
}