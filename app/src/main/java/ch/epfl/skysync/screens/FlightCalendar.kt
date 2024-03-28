package ch.epfl.skysync.screens

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import ch.epfl.skysync.dataModels.calendarModels.TimeSlot
import ch.epfl.skysync.dataModels.flightModels.Flight
import java.time.LocalDate

/**
 * Composable function to display flight information based on a given date and time slot.
 *
 * @param date The date for which the flight information is requested.
 * @param time The time slot AM or PM.
 * @param getFlightByDate A function that retrieves flight information based on date and time.
 *                         It takes a LocalDate and a TimeSlot as input parameters and returns
 *                         a Flight object representing the flight information for the specified
 *                         date and time slot, or null if no flight exists for that date and time slot.
 * @param onClick Lambda function representing the action to perform when the button is clicked.
 */

@Composable
fun ShowFlight(date: LocalDate, time: TimeSlot, getFlightByDate: (LocalDate, TimeSlot) -> Flight?, onClick: () -> Unit) {
    Button(onClick) {
        val flight = getFlightByDate(date, time)
        if(flight == null) {
            Text(text = "No flight")
        } else {
            Text(text = flight.flightType.name)
        }
    }
}
/**
 * Composable function to display a calendar with flight information for each date and time slot.
 *
 * @param onClick Lambda function representing the action to perform when a flight button is clicked.
 * @param getFlightByDate A function that retrieves flight information based on date and time.
 *                         It takes a LocalDate and a TimeSlot as input parameters and returns
 *                         a Flight object representing the flight information for the specified
 *                         date and time slot, or null if no flight exists for that date and time slot.
 */
@Composable
fun ShowFlightCalendar(onClick: () -> Unit, getFlightByDate: (LocalDate, TimeSlot) -> Flight?){
    Calendar {
            date, time, size -> ShowFlight(date, time, getFlightByDate,onClick)
    }
}
