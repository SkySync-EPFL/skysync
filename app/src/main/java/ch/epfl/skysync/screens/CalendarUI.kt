package ch.epfl.skysync.screens
import android.util.Log
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.TimeSlot
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
 * Composable function to display a colored tile indicating availability status.
 *
 * @param date The date for which availability status is being displayed.
 * @param slot The time slot for which availability status is being displayed.
 * @param scaleHeight The scale (in height) of the tile.
 * @param scaleWidth The scale (in width) of the tile.
 * @param viewModel user viewmodel (used to determine availabilities status)
 *
 */
@Composable
fun showTile(date: LocalDate, slot: TimeSlot, scaleHeight: Float,scaleWidth: Float,viewModel: UserViewModel) {
    var availabilityStatus by remember { mutableStateOf(viewModel.user.value.availabilities.getAvailabilityStatus(date, slot)) }
    val backgroundColor =
        when (availabilityStatus) {
            AvailabilityStatus.OK -> customGreen
            AvailabilityStatus.MAYBE -> customBlue
            AvailabilityStatus.NO -> customRed
            null -> customEmpty
        }

    Box(
        modifier = Modifier.fillMaxHeight(scaleHeight).fillMaxWidth(scaleWidth).background(color = backgroundColor,
            shape = RoundedCornerShape(0.dp)).clickable {
            availabilityStatus =  viewModel.user.value.availabilities.nextAvailabilityStatus(date, slot)
        })
}
/**
 * Composable function to display the calendar view.
 * @param today The current date to initialize the calendar.
 * @param viewModel user viewmodel (used to determine availabilities status)
 */
@Composable
fun showCalendarAvailabilities(today: LocalDate,viewModel: UserViewModel) {
    Calendar(today,viewModel)
}
/**
 * Preview function to display the calendar view.
 *
 */
@Composable
@Preview
fun CalendarPreview() {
    var viewModel = UserViewModel.createViewModel(firebaseUser = null)
    viewModel.user.value.availabilities.setAvailabilityByDate(
        LocalDate.now(), TimeSlot.AM, AvailabilityStatus.MAYBE)
    showCalendarAvailabilities(LocalDate.now(),viewModel)
}
/**
 * Composable function to display a calendar view.
 * @param today The current date to initialize the calendar.
 * @param viewModel user viewmodel (used to determine availabilities status)
 */
@Composable
fun Calendar(today : LocalDate,viewModel: UserViewModel) {
    var currentWeekStartDate by remember { mutableStateOf(getStartOfWeek(today)) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
    ) {
        WeekView(currentWeekStartDate,viewModel) // Add key parameter
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
    }
}


/**
 * Composable function to display a week view with customizable tiles for each day.
 *
 * @param startOfWeek the start date of the week.
 * @param viewModel user viewmodel (used to determine availabilities status)
 */
@Composable
fun WeekView(startOfWeek: LocalDate,viewModel: UserViewModel) {
    val weekDays = (0..6).map { startOfWeek.plusDays(it.toLong()) }
    Column(modifier = Modifier.fillMaxHeight(0.6f)) {
        Row(
        ) {
            Spacer(modifier = Modifier.width(120.dp))
            Text(
                modifier = Modifier.fillMaxWidth(0.5f),
                text = "AM",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.fillMaxWidth(1f),
                text = "PM",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
        weekDays.withIndex().forEach { (i,day) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = day.format(DateTimeFormatter.ofPattern("EEE, MM/dd", Locale.getDefault())),
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.width(120.dp)
                )
                Log.d("ret",i.toString())
                val scale=(1f/7*7/(7-i))
                Column(modifier = Modifier.width(1.dp).fillMaxHeight(scale).background(Color.Black)){}
                showTile(day, TimeSlot.AM, scale,0.5f,viewModel)
                Column(modifier = Modifier.width(1.dp).fillMaxHeight(scale).background(Color.Black)){}
                showTile(day, TimeSlot.PM, scale,1f,viewModel)
            }
            Column(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color.Black)){}
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

/**
 * Determines the availability status for a given date and time slot.
 * Dummy function that always returns AvailabilityStatus.OK.
 *
 * @param date The date for which availability status is being checked.
 * @param slot The time slot for which availability status is being checked.
 * @return The availability status for the specified date and time slot.
 */
fun getAvailabilityStatus(date: LocalDate, slot: TimeSlot): AvailabilityStatus? {
    return AvailabilityStatus.OK
}
/**
 * Determines the availability status for a given date and time slot.
 * Dummy function that always returns AvailabilityStatus.NO.
 *
 * @param date The date for which availability status is being checked.
 * @param slot The time slot for which availability status is being checked.
 * @return The availability status for the specified date and time slot.
 */
fun nextAvailabilityStatus(date: LocalDate, slot: TimeSlot): AvailabilityStatus? {
    return null
}