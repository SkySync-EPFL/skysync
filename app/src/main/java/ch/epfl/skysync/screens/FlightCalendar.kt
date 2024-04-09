package ch.epfl.skysync.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.navigation.Route
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
fun ShowFlight(
    date: LocalDate,
    time: TimeSlot,
    getFlightByDate: (LocalDate, TimeSlot) -> Flight?,
    onClick: () -> Unit
) {
    val flight = getFlightByDate(date, time)
    if(flight != null){
        Button(
            onClick,
            shape = RectangleShape,
            modifier = Modifier
                .size(105.dp, 100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ){
            Text(text = flight.flightType.name)
        }
    }
    else{
        Box(
            modifier = Modifier
                .size(105.dp, 100.dp),
            contentAlignment = Alignment.Center
        ) {
        }
    }
}
/**
 * Composable function to display a calendar with flight information for each date and time slot.
 *
 * @param onClick Lambda function representing the action to perform when a flight button is
 *   clicked.
 * @param getFlightByDate A function that retrieves flight information based on date and time. It
 *   takes a LocalDate and a TimeSlot as input parameters and returns a Flight object representing
 *   the flight information for the specified date and time slot, or null if no flight exists for
 *   that date and time slot.
 */
@Composable
fun ShowFlightCalendar(
    onClick: () -> Unit,
    getFlightByDate: (LocalDate, TimeSlot) -> Flight?,
    navController: NavHostController
) {
    Box() {
        Calendar(navController)
        Button(
            onClick = {},
            shape = CircleShape,
            colors = ButtonDefaults.outlinedButtonColors(containerColor=  Color.Gray),
            modifier = Modifier
                .padding(25.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "content description", tint = Color.DarkGray)
        }
    }
}

@Composable
@Preview
fun ShowFlightCalendarPreview() {
  val navController = rememberNavController()
  ShowFlightCalendar({}, FakeFlights, navController = navController)
}

val FakeFlights: (LocalDate, TimeSlot) -> Flight? = { date: LocalDate, timeslot: TimeSlot ->
  if ((LocalDate.now() == date) && (timeslot == TimeSlot.AM)) {
    PlannedFlight(
        "Test Flight",
        1,
        Team(mutableListOf(Role(RoleType.PILOT))),
        FlightType("Premium"),
        null,
        null,
        date,
        TimeSlot.AM,
        mutableListOf(Vehicle("car", "car1")))
  } else {
    null
  }
}

@Composable
fun Calendar(navController: NavHostController) {
    var currentWeekStartDate by remember { mutableStateOf(getStartOfWeek(LocalDate.now())) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
    ) {
        WeekView(currentWeekStartDate, key = currentWeekStartDate.toString()) // Add key parameter
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween)
        {
            Spacer(modifier = Modifier.width(5.dp))
            Button(
                onClick = { currentWeekStartDate = currentWeekStartDate.minusWeeks(1) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)))
            {
                Text(
                    text = "Prev Week",
                    color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.width(20.dp))
            Button(
                onClick = { currentWeekStartDate = currentWeekStartDate.plusWeeks(1) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))) {
                Text(
                    text = "Next Week",
                    color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.width(5.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp), // Adjust bottom padding as needed
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier
                    .background(Color.LightGray, RoundedCornerShape(16.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(16.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .size(width = 170.dp, height = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Flight Calendar",
                        fontSize = 12.sp,
                        color = Color(0xFFFFA500),
                        modifier = Modifier
                            .padding(8.dp),
                        overflow = TextOverflow.Clip
                    )
                }
                Button(
                    onClick = { navController.navigate(Route.CALENDAR) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(width = 170.dp, height = 40.dp),
                ) {
                    Text(
                        text = "Avaliability Calendar",
                        fontSize = 12.sp,
                        color = Color.Black,
                        overflow = TextOverflow.Clip,
                        maxLines = 1
                    )
                }
            }
        }
    }
}


/**
 * Composable function to display a week view with customizable tiles for each day.
 *
 * @param startOfWeek the start date of the week.
 * @param key the key for the composable.
 */
@Composable
fun WeekView(startOfWeek: LocalDate, key: String = startOfWeek.toString()) {
    val weekDays = (0..6).map { startOfWeek.plusDays(it.toLong()) }
    Column(){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.width(30.dp))
            Box(
                modifier = Modifier
                    .size(30.dp, 50.dp)
                    .padding(vertical = 1.dp),
                contentAlignment = Alignment.Center)
            {
                Text(
                    text = "AM",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Box(
                modifier = Modifier
                    .size(30.dp, 50.dp)
                    .padding(vertical = 1.dp),
                contentAlignment = Alignment.Center)
            {
                Text(
                    text = "PM",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(1.dp))
        }
        weekDays.forEach { day ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(200.dp, 70.dp)
                    .padding(vertical = 1.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .size(120.dp, 70.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center ) {
                        Text(
                            text = day.format(
                                DateTimeFormatter.ofPattern(
                                    "EEE",
                                    Locale.getDefault()
                                )
                            ),
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier.width(120.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center)
                    {
                        Text(
                            text = day.format(
                                DateTimeFormatter.ofPattern(
                                    "MM/dd",
                                    Locale.getDefault()
                                )
                            ),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.width(120.dp),
                                    textAlign = TextAlign.Center
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ShowFlight(day, TimeSlot.AM, FakeFlights, {})
                    ShowFlight(day, TimeSlot.PM, FakeFlights, {})
                    Spacer(modifier = Modifier.width(1.dp))
                }
            }
            Divider(color = Color.Black, thickness = 1.dp)
        }
    }
}
