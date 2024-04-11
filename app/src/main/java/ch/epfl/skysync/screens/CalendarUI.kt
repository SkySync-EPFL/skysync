package ch.epfl.skysync.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.viewmodel.UserViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// Define custom colors to represent availability status
// These colors are used to indicate availability status in the UI

// Custom color for indicating availability status as "OK" (e.g., available)
val customGreen = Color(android.graphics.Color.parseColor("#aaee7b"))

// Custom color for indicating availability status as "MAYBE" (e.g., partially available)
val customBlue = Color(android.graphics.Color.parseColor("#9ae0f0"))

// Custom color for indicating availability status as "NO" (e.g., not available)
val customRed = Color(android.graphics.Color.parseColor("#f05959"))

// Custom color for indicating an empty or unknown availability status
val customEmpty = Color(android.graphics.Color.parseColor("#f0f0f0"))
/**
 * Maps availability status to a corresponding color.
 *
 * @param status The availability status to be mapped.
 * @return The color representing the availability status.
 */
fun availabilityToColor(status: AvailabilityStatus): Color {
  if (status == AvailabilityStatus.OK) {
    return customGreen
  }
  if (status == AvailabilityStatus.MAYBE) {
    return customBlue
  }
  if (status == AvailabilityStatus.NO) {
    return customRed
  } else {
    return customEmpty
  }
}
/**
 * Composable function to display a colored tile indicating availability status.
 *
 * @param date The date for which availability status is being displayed.
 * @param slot The time slot for which availability status is being displayed.
 * @param scaleHeight The scale (in height) of the tile.
 * @param scaleWidth The scale (in width) of the tile.
 * @param viewModel user viewmodel (used to determine availabilities status)
 */
@Composable
fun showTile(
    date: LocalDate,
    slot: TimeSlot,
    scaleHeight: Float,
    scaleWidth: Float,
    viewModel: UserViewModel,
    index: Int
) {
  var colorUp by remember {
    mutableStateOf(
        availabilityToColor(viewModel.user.value.availabilities.getAvailabilityStatus(date, slot)))
  }
  colorUp =
      availabilityToColor(viewModel.user.value.availabilities.getAvailabilityStatus(date, slot))
  Box(
      modifier =
          Modifier.fillMaxHeight(scaleHeight)
              .testTag(date.toString() + slot.toString())
              .fillMaxWidth(scaleWidth)
              .background(color = colorUp, shape = RoundedCornerShape(0.dp))
              .clickable {
                colorUp =
                    availabilityToColor(
                        viewModel.user.value.availabilities.nextAvailabilityStatus(date, slot))
              })
}
/**
 * Composable function to display the calendar view.
 *
 * @param today The current date to initialize the calendar.
 * @param viewModel user viewmodel (used to determine availabilities status)
 */
@Composable
fun showCalendarAvailabilities(
    navHostController: NavHostController,
    padding: PaddingValues,
    viewModel: UserViewModel
) {
  val today = LocalDate.now()
  Calendar(navHostController, today, viewModel)
}
/** Preview function to display the calendar view. */
// @Composable
// @Preview
// fun CalendarPreview() {
//  var viewModel = UserViewModel.createViewModel(firebaseUser = null)
//  viewModel.user.value.availabilities.setAvailabilityByDate(
//      LocalDate.now(), TimeSlot.AM, AvailabilityStatus.MAYBE)
//  showCalendarAvailabilities(viewModel)
// }
/**
 * Composable function to display a calendar view.
 *
 * @param today The current date to initialize the calendar.
 * @param viewModel user viewmodel (used to determine availabilities status)
 */
@Composable
fun Calendar(navHostController: NavHostController, today: LocalDate, viewModel: UserViewModel) {
  var currentWeekStartDate by remember { mutableStateOf(getStartOfWeek(today)) }

  Column(
      modifier = Modifier.fillMaxSize().padding(16.dp),
  ) {
    WeekView(currentWeekStartDate, viewModel)
    Spacer(modifier = Modifier.height(8.dp))
    Row {
      Spacer(modifier = Modifier.width(138.dp))
      Button(onClick = { currentWeekStartDate = currentWeekStartDate.minusWeeks(1) }) {
        Text("Prev Week")
      }
      Spacer(modifier = Modifier.width(20.dp))
      Button(onClick = { currentWeekStartDate = currentWeekStartDate.plusWeeks(1) }) {
        Text("Next Week")
      }
      Spacer(modifier = Modifier.width(4.dp))
    }
    Row {
      Button(onClick = { navHostController.navigate(Route.PERSONAL_FLIGHT_CALENDAR) }) {
        Text(text = "Flight Calendar")
      }
      Button(onClick = { navHostController.navigate(Route.AVAILABILITY_CALENDAR) }) {
        Text(text = "Availability")
      }
    }
  }
}

/**
 * Composable function to display a week view with customizable tiles for each day.
 *
 * @param startOfWeek the start date of the week.
 * @param viewModel user viewmodel (used to determine availabilities status)
 */
@Composable
fun WeekView(startOfWeek: LocalDate, viewModel: UserViewModel) {
  val weekDays = (0..6).map { startOfWeek.plusDays(it.toLong()) }
  Column(modifier = Modifier.fillMaxHeight(0.7f)) {
    Row() {
      Spacer(modifier = Modifier.width(120.dp))
      Text(
          modifier = Modifier.fillMaxWidth(0.5f),
          text = "AM",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = Color.Black,
          textAlign = TextAlign.Center)
      Text(
          modifier = Modifier.fillMaxWidth(1f),
          text = "PM",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = Color.Black,
          textAlign = TextAlign.Center)
    }
    weekDays.withIndex().forEach { (i, day) ->
      Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
            text = day.format(DateTimeFormatter.ofPattern("EEE, MM/dd", Locale.getDefault())),
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.width(120.dp))
        val scale = (1f / 7 * 7 / (7 - i))
        Column(modifier = Modifier.width(1.dp).fillMaxHeight(scale).background(Color.Black)) {}
        val amIndex = i * 2
        showTile(day, TimeSlot.AM, scale, 0.5f, viewModel, amIndex)
        Column(modifier = Modifier.width(1.dp).fillMaxHeight(scale).background(Color.Black)) {}
        val pmIndex = i * 2 + 1
        showTile(day, TimeSlot.PM, scale, 1f, viewModel, pmIndex)
      }
      Column(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color.Black)) {}
    }
  }
}

/**
 * Function to calculate the start date of the week for the given date.
 *
 * @param date The input LocalDate for which the start date of the week is to be calculated.
 * @return The start date of the week containing the input date.
 */
fun getStartOfWeek(date: LocalDate): LocalDate {
  return date.minusDays(date.dayOfWeek.value.toLong() - 1)
}
