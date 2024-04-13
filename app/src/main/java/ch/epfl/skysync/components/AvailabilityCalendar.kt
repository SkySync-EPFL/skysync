package ch.epfl.skysync.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.screens.SwitchButton
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
 * @param date The date for which availability status is being displayed.
 * @param slot The time slot for which availability status is being displayed.
 * @param scaleHeight The scale (in height) of the tile.
 * @param scaleWidth The scale (in width) of the tile.
 * @param viewModel user viewmodel (used to determine availabilities status)
 */
@Composable
fun AvailabilityTile(
    date: LocalDate,
    time: TimeSlot,
    availabilityStatus: AvailabilityStatus,
    onClick: () -> AvailabilityStatus
) {
    var status by remember {
        mutableStateOf(
            availabilityStatus
        )
    }
    var weight = 0.5f
    if (time == TimeSlot.PM) {
        weight = 1f
    }
    Box(
        modifier =
        Modifier
            .fillMaxHeight()
            .fillMaxWidth(weight)
            .testTag(date.toString() + time.toString())
            .background(
                color = availabilityToColor(status),
                shape = RoundedCornerShape(0.dp)
            )
            .clickable {
                status = onClick()
                println("clicked: $status")
            })
}

/**
 * Composable function to display a calendar with flight information for each date and time slot.
 *
 * @param navController The navigation controller used for navigating to different destinations
 *   within the app.
 */
@Composable
fun AvailabilityCalendar(
    viewModel: CalendarViewModel,
    padding: PaddingValues,
    navController: NavHostController,
) {
    ModularCalendar({
        SwitchButton(
            Availability = true,
            padding = padding,
            onClick = { navController.navigate(Route.PERSONAL_FLIGHT_CALENDAR) },
            onClickRight = { })
    }) { date, time ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        println("AvailabilityCalendar user: ${uiState.user}")
        val availabilityCalendar = uiState.user?.availabilities
        val availabilityStatus = availabilityCalendar?.getAvailabilityStatus(date, time)
            ?: AvailabilityStatus.UNDEFINED

        AvailabilityTile(date = date, time = time, availabilityStatus = availabilityStatus) {
            return@AvailabilityTile availabilityCalendar?.nextAvailabilityStatus(date, time)
                ?: AvailabilityStatus.UNDEFINED
        }
    }
}