package ch.epfl.skysync.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import ch.epfl.skysync.dataModels.calendarModels.AvailabilityStatus
import ch.epfl.skysync.dataModels.calendarModels.TimeSlot
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
/**
 * Determines the availability status for a given date and time slot.
 *
 * @param date The date for which availability status is being checked.
 * @param slot The time slot for which availability status is being checked.
 * @return The availability status for the specified date and time slot.
 */
fun getAvailabilityStatus(date : LocalDate, slot : TimeSlot): AvailabilityStatus{
    return AvailabilityStatus.MAYBE
}
/**
 * Determines the availability status for a given date and time slot.
 *
 * @param date The date for which availability status is being checked.
 * @param slot The time slot for which availability status is being checked.
 * @return The availability status for the specified date and time slot.
 */
fun nextAvailabilityStatus(date : LocalDate, slot : TimeSlot): AvailabilityStatus{
    return AvailabilityStatus.OK
}

/**
 * Composable function to display a colored tile indicating availability status.
 *
 * @param date The date for which availability status is being displayed.
 * @param slot The time slot for which availability status is being displayed.
 * @param size The size of the tile.
 */
@Composable
fun showTile(date: LocalDate, slot : TimeSlot, size: Dp) {
    var availabilityStatus by remember { mutableStateOf(getAvailabilityStatus(date, slot)) }

    val backgroundColor = when (availabilityStatus) {
        AvailabilityStatus.OK -> Color.Green
        AvailabilityStatus.MAYBE -> Color.Blue
        AvailabilityStatus.NO -> Color.Red
    }

    Box(
        modifier = Modifier
            .size(size)
            .background(backgroundColor)
            .clickable {
                availabilityStatus = nextAvailabilityStatus(date, slot)
            }
    )
}







@Composable
fun showCalendarAvailabilities(){
    Calendar {
             date,slot,size -> showTile(date, slot,size)
    }
}



/**
 * Composable function to display a calendar view.
 * @param modularShow a Composable function that what to display each tile in the calendar depending
 * on the day and if it's AM/PM (AM=0:00 and PM=12:00).
 */
@Composable
fun Calendar(modularShow: @Composable (LocalDate,TimeSlot, Dp) -> Unit) {
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
fun WeekView(startOfWeek: LocalDate, modularShow: @Composable (LocalDate,TimeSlot, Dp) -> Unit) {
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
            modularShow(day,TimeSlot.AM, 25.dp)
            modularShow(day,TimeSlot.PM, 25.dp)
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