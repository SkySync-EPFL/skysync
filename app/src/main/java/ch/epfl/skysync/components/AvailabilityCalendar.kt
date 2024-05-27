package ch.epfl.skysync.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.ui.theme.darkOrange
import java.time.LocalDate

// Define custom colors to represent availability status
// These colors are used to indicate availability status in the UI

// Custom color for indicating availability status as "OK" (e.g., available)
val customGreen = Color(0xffaaee7b)

// Custom color for indicating availability status as "MAYBE" (e.g., partially available)
val customBlue = Color(0xff9ae0f0)

// Custom color for indicating availability status as "NO" (e.g., not available)
val customRed = Color(0xfff05959)

// Custom color for indicating an empty or unknown availability status
val customEmpty = Color(0xfff0f0f0)

/**
 * Maps availability status to a corresponding color.
 *
 * @param status The availability status to be mapped.
 * @return The color representing the availability status.
 */
fun availabilityToColor(status: AvailabilityStatus): Color {
  val availabilityToColorMap =
      mapOf(
          AvailabilityStatus.OK to customGreen,
          AvailabilityStatus.MAYBE to customBlue,
          AvailabilityStatus.NO to customRed,
          AvailabilityStatus.ASSIGNED to darkOrange)
  return availabilityToColorMap.getOrDefault(status, customEmpty)
}

/**
 * Maps availability status to a corresponding color.
 *
 * @param status The availability status to be mapped.
 * @return The color representing the availability status.
 */
fun availabilityToText(status: AvailabilityStatus): String {
  val availabilityToColorMap =
      mapOf(
          AvailabilityStatus.OK to "OK",
          AvailabilityStatus.MAYBE to "Maybe",
          AvailabilityStatus.NO to "NO",
          AvailabilityStatus.ASSIGNED to "Assigned")
  return availabilityToColorMap.getOrDefault(status, "")
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

  val weight = if (time == TimeSlot.AM) 0.5f else 1f
  val testTag = date.toString() + time.toString()
  Box(
      modifier =
          Modifier.fillMaxHeight()
              .fillMaxWidth(weight)
              .testTag(testTag)
              .background(
                  color = availabilityToColor(availabilityStatus), shape = RoundedCornerShape(0.dp))
              .clickable { onClick() }) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = availabilityToText(availabilityStatus))
            }
      }
}

/**
 * Composable function to display buttons for saving or canceling availabilities.
 *
 * @param isDraft Indicates if the [AvailabilityCalendar] is being modified
 * @param onSave Callback called when clicking on the save button, should save the
 * * [AvailabilityCalendar] to the database
 *
 * @param onCancel Callback called when clicking on the cancel button, should reset the
 * * [AvailabilityCalendar] to its original state
 */
@Composable
fun SaveCancelButton(
    connectivityStatus: ConnectivityStatus,
    isDraft: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
  Row(
      modifier = Modifier.padding(8.dp),
  ) {
    Button(
        onClick = onCancel,
        enabled = isDraft && connectivityStatus.isOnline(),
        colors = ButtonDefaults.buttonColors(containerColor = darkOrange),
        shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp),
        modifier = Modifier.weight(1f).testTag("CancelButton"),
    ) {
      Text(text = "Cancel", fontSize = 12.sp, overflow = TextOverflow.Clip)
    }
    Button(
        onClick = onSave,
        enabled = isDraft && connectivityStatus.isOnline(),
        shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp),
        modifier = Modifier.weight(1f).testTag("SaveButton"),
    ) {
      Text(
          text = "Save",
          fontSize = 12.sp,
          overflow = TextOverflow.Clip,
      )
    }
  }
}

/**
 * Composable function to display a calendar with the user's availabilities for each date and time
 * slot.
 *
 * @param padding The padding of the surrounding Scafffold
 * @param getAvailabilityStatus Callback that returns the [AvailabilityStatus] for a specific date
 *   and time slot
 * @param nextAvailabilityStatus Callback called when clicking on an tile, returns the next
 *   [AvailabilityStatus], should also update the [AvailabilityCalendar]
 * @param onSave Callback called when clicking on the save button, should save the
 *   [AvailabilityCalendar] to the database
 *     @param onCancel Callback called when clicking on the cancel button, should reset the
 *       [AvailabilityCalendar] to its original state
 */
@Composable
fun AvailabilityCalendar(
    padding: PaddingValues,
    getAvailabilityStatus: (LocalDate, TimeSlot) -> AvailabilityStatus,
    nextAvailabilityStatus: (LocalDate, TimeSlot) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    connectivityStatus: ConnectivityStatus
) {
  var isDraft by remember { mutableStateOf(false) }

  Column(modifier = Modifier.padding(padding)) {
    ModularCalendar(modifier = Modifier.weight(1f), isDraft = isDraft) { date, time ->
      val availabilityStatus = getAvailabilityStatus(date, time)
      AvailabilityTile(date = date, time = time, availabilityStatus = availabilityStatus) {
        if (connectivityStatus.isOnline()) {
          nextAvailabilityStatus(date, time)
          isDraft = true
        }
      }
    }
    SaveCancelButton(
        connectivityStatus,
        isDraft = isDraft,
        onSave = {
          onSave()
          isDraft = false
        }) {
          onCancel()
          isDraft = false
        }
  }
}
