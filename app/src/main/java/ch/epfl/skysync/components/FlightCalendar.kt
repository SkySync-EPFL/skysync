package ch.epfl.skysync.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.viewmodel.CalendarViewModel
import java.time.LocalDate

/**
 * Composable function to display flight information based on a given date and time slot.
 *
 * @param date The date of the tile
 * @param time The time slot of the tile
 * @param flight The flight at this tile, if any
 * @param onClick Callback called when clicking on the tile
 */
@Composable
fun FlightTile(date: LocalDate, time: TimeSlot, flight: Flight?, onClick: () -> Unit) {
  var weight = 0.5f
  val lightgrey = Color(0xfff0f0f0)
  if (time == TimeSlot.PM) {
    weight = 1f
  }
  if (flight != null) {
    Button(
        onClick = { onClick },
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
        modifier = Modifier.background(lightgrey).fillMaxHeight().fillMaxWidth(weight),
        contentAlignment = Alignment.Center) {}
  }
}

/**
 * Composable function to display a calendar with flight information for each date and time slot.
 *
 * @param onAvailabilityCalendarClick Callback called when clicking the button to navigate to the
 *   Availability calendar
 * @param getFirstFlightByDate Callback that returns the first [Flight] (if any) for a specific date
 *   and time slot
 * @param onFlightClick Callback called when clicking on a tile with a flight
 */
@Composable
fun FlightCalendar(
    padding: PaddingValues,
    onAvailabilityCalendarClick: () -> Unit,
    getFirstFlightByDate: (LocalDate, TimeSlot) -> Flight?,
    onFlightClick: (Flight) -> Unit
) {
  ModularCalendar(
      padding = padding,
      bottom = {
        SwitchButtonBetter(
            currentSide = Side.LEFT,
            textLeft = "Flight Calendar",
            textRight = "Availability Calendar",
            onClickLeft = {},
            onClickRight = onAvailabilityCalendarClick)
      }) { date, time ->
        val flight = getFirstFlightByDate(date, time)
        FlightTile(
            date = date,
            time = time,
            flight = flight,
        ) {
          onFlightClick(flight!!)
        }
      }
}

@Composable
fun FlightCalendarNew(
    topBar: @Composable () -> Unit,
    navController: NavHostController,
    viewModel: CalendarViewModel,
    onFlightClick: (Flight) -> Unit
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val flightCalendar = uiState.flightGroupCalendar

  Scaffold(topBar = { topBar() }, bottomBar = { BottomBar(navController) }) { padding ->
    ModularCalendarNew(padding = padding, isDraft = false) { date, time ->
      val flight = flightCalendar.getFirstFlightByDate(date, time)
      FlightTile(
          date = date,
          time = time,
          flight = flight,
      ) {
        onFlightClick(flight!!)
      }
    }
  }
}
