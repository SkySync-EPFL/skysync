package ch.epfl.skysync.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.viewmodel.UserViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Composable function to display flight information based on a given date and time slot.
 *
 * @param date The date for which the flight information is requested.
 * @param time The time slot AM or PM.
 * @param getFlightByDate A function that retrieves flight information based on date and time. It
 *   takes a LocalDate and a TimeSlot as input parameters and returns a Flight object representing
 *   the flight information for the specified date and time slot, or null if no flight exists for
 *   that date and time slot.
 * @param onClick Lambda function representing the action to perform when the button is clicked.
 */
@Composable
fun ShowFlight(date: LocalDate, time: TimeSlot, viewModel: UserViewModel) {
  val flight = viewModel.user.value.assignedFlights.getFirstFlightByDate(date, time)
  var weight = 0.5f
  if (time == TimeSlot.PM) {
    weight = 1f
  }
  if (flight != null) {
    Button(
        onClick = {},
        shape = RectangleShape,
        modifier = Modifier.fillMaxHeight().fillMaxWidth(weight),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan)) {
          Text(
              text = flight.flightType.name,
              fontSize = 12.sp,
              color = Color.Black,
              overflow = TextOverflow.Clip,
              maxLines = 1,
              modifier = Modifier.width(120.dp),
              textAlign = TextAlign.Center)
        }
  } else {
    Box(
        modifier = Modifier.fillMaxHeight().fillMaxWidth(weight),
        contentAlignment = Alignment.Center) {}
  }
}
/**
 * Composable function to display the flight calendar screen.
 *
 * @param navController The navigation controller used for navigating to different destinations
 *   within the app.
 */
@Composable
fun ShowFlightCalendar(navController: NavHostController, viewModel: UserViewModel) {
  Scaffold(bottomBar = { BottomBar(navController) }) { padding ->
    Box() { Calendar(navController, viewModel, padding) }
  }
}

/**
 * Composable function to display a calendar with flight information for each date and time slot.
 *
 * @param navController The navigation controller used for navigating to different destinations
 *   within the app.
 */
@Composable
fun Calendar(navController: NavHostController, viewModel: UserViewModel, padding: PaddingValues) {
  var currentWeekStartDate by remember { mutableStateOf(getStartOfWeek(LocalDate.now())) }
  Column(
      modifier = Modifier.fillMaxSize().padding(16.dp).background(Color.White),
  ) {
    WeekView(currentWeekStartDate, viewModel, navController)
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
  }
}

/**
 * Composable function to display a week view with flight information for each day and time slot.
 *
 * @param startOfWeek The start date of the week to be displayed.
 */
@Composable
fun WeekView(startOfWeek: LocalDate, viewModel: UserViewModel, navController: NavHostController) {
  val weekDays = (0..6).map { startOfWeek.plusDays(it.toLong()) }
  Column() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
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
              ShowFlight(day, TimeSlot.AM, viewModel)
              ShowFlight(day, TimeSlot.PM, viewModel)
            }
      }
      Divider(color = Color.Black, thickness = 1.dp)
    }
  }
}
