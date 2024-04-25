package ch.epfl.skysync.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.viewmodel.CalendarViewModel
import java.time.LocalDate

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
 * @param date The date of the tile
 * @param time The time slot of the tile
 * @param availabilityStatus The availability status of the tile
 * @param onClick Callback called when clicking on the tile
 */
@Composable
fun AvailabilityTile(
    date: LocalDate,
    time: TimeSlot,
    availabilityStatus: AvailabilityStatus,
    onClick: () -> Unit
) {
  var weight = 0.5f
  if (time == TimeSlot.PM) {
    weight = 1f
  }
  Box(
      modifier =
          Modifier.fillMaxHeight()
              .fillMaxWidth(weight)
              .testTag(date.toString() + time.toString())
              .background(
                  color = availabilityToColor(availabilityStatus), shape = RoundedCornerShape(0.dp))
              .clickable { onClick() })
}

/**
 * Composable function to display a calendar with the user's availabilities for each date and time
 * slot.
 *
 * @param onFlightCalendarClick Callback called when clicking the button to navigate to the Flight
 *   calendar
 * @param getAvailabilityStatus Callback that returns the [AvailabilityStatus] for a specific date
 *   and time slot
 * @param nextAvailabilityStatus Callback called when clicking on an tile, returns the next
 *   [AvailabilityStatus], should also update the [AvailabilityCalendar]
 * @param onSave Callback called when clicking on the save button, should save the
 *   [AvailabilityCalendar] to the database
 */
@Composable
fun AvailabilityCalendar(
    padding: PaddingValues,
    onFlightCalendarClick: () -> Unit,
    getAvailabilityStatus: (LocalDate, TimeSlot) -> AvailabilityStatus,
    nextAvailabilityStatus: (LocalDate, TimeSlot) -> AvailabilityStatus,
    onSave: () -> Unit
) {
  ModularCalendar(
      padding = padding,
      bottom = {
        Column() {
          // TODO: Proper UI for this button
          Button(onClick = onSave, modifier = Modifier.testTag("AvailabilityCalendarSaveButton")) {
            Text(text = "Save")
          }
          SwitchButtonBetter(
              currentSide = Side.RIGHT,
              textLeft = "Flight Calendar",
              textRight = "Availability Calendar",
              onClickLeft = onFlightCalendarClick,
              onClickRight = {})
        }
      }) { date, time ->
        // at the moment the Calendar is a mutable class
        // thus the reference of the Calendar stay the same on updates
        // -> it does not trigger a recompose. To trigger the recompose
        // we have to store the availability status in a state and update
        // it each time the result of getAvailabilityStatus change
        // which is a bit hacky and should be a temporary solution
        val availabilityStatus = getAvailabilityStatus(date, time)
        var status by remember { mutableStateOf(availabilityStatus) }
        if (status != availabilityStatus) {
          status = availabilityStatus
        }
        AvailabilityTile(date = date, time = time, availabilityStatus = status) {
          status = nextAvailabilityStatus(date, time)
        }
      }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AvailabilityCalendarNew(
    topBar: @Composable () -> Unit,
    navController: NavHostController,
    viewModel: CalendarViewModel,
    onSave: () -> Unit
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val availabilityCalendar = uiState.availabilityCalendar
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = { topBar() },
      bottomBar = { BottomBar(navController) }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
          ModularCalendarNew() { date, time ->
            // at the moment the Calendar is a mutable class
            // thus the reference of the Calendar stay the same on updates
            // -> it does not trigger a recompose. To trigger the recompose
            // we have to store the availability status in a state and update
            // it each time the result of getAvailabilityStatus change
            // which is a bit hacky and should be a temporary solution
            val availabilityStatus = availabilityCalendar.getAvailabilityStatus(date, time)
            var status by remember { mutableStateOf(availabilityStatus) }
            if (status != availabilityStatus) {
              status = availabilityStatus
            }
            AvailabilityTile(date = date, time = time, availabilityStatus = status) {
              status = availabilityCalendar.nextAvailabilityStatus(date, time)
            }
          }

          Button(onClick = onSave, modifier = Modifier.testTag("AvailabilityCalendarSaveButton")) {
            Text(text = "Save")
          }
        }
      }
}
