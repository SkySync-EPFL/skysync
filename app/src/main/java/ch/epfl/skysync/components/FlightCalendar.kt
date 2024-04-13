package ch.epfl.skysync.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.navigation.NavHostController
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.screens.SwitchButton
import ch.epfl.skysync.viewmodel.CalendarViewModel
import java.time.LocalDate

/**
 * Composable function to display flight information based on a given date and time slot.
 *
 * @param date The date for which the flight information is requested.
 * @param time The time slot AM or PM.
 * @param onClick Lambda function representing the action to perform when the button is clicked.
 */
@Composable
fun FlightTile(date: LocalDate, time: TimeSlot, flight: Flight?, onClick: () -> Unit) {
    var weight = 0.5f
    if (time == TimeSlot.PM) {
        weight = 1f
    }
    if (flight != null) {
        Button(
            onClick = {onClick},
            shape = RectangleShape,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(weight),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan)
        ) {
            Text(
                text = flight.flightType.name,
                fontSize = 12.sp,
                color = Color.Black,
                overflow = TextOverflow.Clip,
                maxLines = 1,
                modifier = Modifier.width(120.dp),
                textAlign = TextAlign.Center
            )
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(weight),
            contentAlignment = Alignment.Center
        ) {}
    }
}

/**
 * Composable function to display a calendar with flight information for each date and time slot.
 *
 * @param navController The navigation controller used for navigating to different destinations
 *   within the app.
 */
@Composable
fun FlightCalendar(
    viewModel: CalendarViewModel,
    padding: PaddingValues,
    navController: NavHostController,
    onFlightClick: (Flight) -> Unit
) {
    ModularCalendar(
        {
            SwitchButton(
                Availability = false,
                padding = padding,
                onClick = {},
                onClickRight = { navController.navigate(Route.AVAILABILITY_CALENDAR) })
        }
    ) { date, time ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val flight = uiState.flightGroupCalendar!!.getFirstFlightByDate(date, time)

        FlightTile(
            date = date,
            time = time,
            flight = flight,
        ) { onFlightClick(flight!!) }
    }
}