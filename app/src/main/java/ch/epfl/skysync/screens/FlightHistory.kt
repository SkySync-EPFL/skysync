package ch.epfl.skysync.screens

import android.location.Location
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.R
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.components.forms.TitledDropDownMenu
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.BASE_ROLES
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.ui.theme.veryLightBlue
import ch.epfl.skysync.ui.theme.veryLightJasmine
import ch.epfl.skysync.ui.theme.veryLightRed
import ch.epfl.skysync.ui.theme.veryLightSatin
import ch.epfl.skysync.ui.theme.veryLightYellow
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun FlightHistoryScreen(
    navController: NavHostController,
    allFlights: List<FinishedFlight> = emptyList()
) {
  Scaffold(topBar = { CustomTopAppBar(navController = navController, title = "Flight History") }) {
      padding ->
    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
      if (allFlights.isEmpty()) {
        Text(modifier = Modifier.padding(padding), text = "No flights available")
      } else {
        var beginDate: LocalDate? by remember { mutableStateOf(null) }
        var endDate: LocalDate? by remember { mutableStateOf(null) }
        var beginFlightTime: LocalTime? by remember { mutableStateOf(null) }
        var endFlightTime: LocalTime? by remember { mutableStateOf(null) }
        var flightType: FlightType? by remember { mutableStateOf(null) }
        var showFilters by remember { mutableStateOf(false) }
        if (showFilters) {
          FiltersMenu(
              onDismissRequest = { showFilters = false },
              onConfirmRequest = {
                  tmpBeginDate,
                  tmpEndDate,
                  tmpBeginFlightTime,
                  tmpEndFlightTime,
                  tmpFlightType ->
                beginDate = tmpBeginDate
                endDate = tmpEndDate
                beginFlightTime = tmpBeginFlightTime
                endFlightTime = tmpEndFlightTime
                flightType = tmpFlightType
                showFilters = false
              })
        }
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
          FlightSearchBar(
              modifier = Modifier.fillMaxWidth().weight(3f),
              onSearch = { query -> allFlights.filter { it.date.toString().contains(query) } },
              results = allFlights)
          IconButton(
              modifier = Modifier.testTag("Filter Button"),
              onClick = { showFilters = !showFilters },
              content = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_filter_list_alt_24),
                    contentDescription = "Filters")
              })
        }
      }
      LazyColumn() {
        itemsIndexed(allFlights) { id, flight -> HistoryCard(flight, Modifier.testTag("Card $id")) }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryCard(flight: FinishedFlight, modifier: Modifier) {

  Card(
      modifier = modifier.padding(horizontal = 16.dp).fillMaxWidth(),
      onClick = { /* TODO Show details of the flight ? */},
      colors = CardDefaults.cardColors(containerColor = cardColors(flight.flightType)),
      elevation = CardDefaults.cardElevation(5.dp),
      border = BorderStroke(1.dp, Color.LightGray),
      shape = RoundedCornerShape(8.dp),
  ) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        Text(
            modifier = Modifier.fillMaxWidth().weight(1f),
            text = getFormattedDate(flight.date),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge)

        Text(
            modifier = Modifier.fillMaxWidth().weight(1f),
            text = flight.flightType.name,
            textAlign = TextAlign.Center)
      }
      Divider(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp),
          thickness = 1.dp,
          color = Color.DarkGray)
      Row(
          modifier = Modifier.height(IntrinsicSize.Min),
          horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
              Text(
                  modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                  text = "Take off : ",
                  textAlign = TextAlign.Left)
              Row() {
                Text(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    text = flight.takeOffTime.format(DateTimeFormatter.ofPattern("HH:mm")))
                Text(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    text = flight.takeOffLocation.provider.toString())
              }
            }
            Divider(Modifier.fillMaxHeight().width(1.dp), color = Color.DarkGray)
            Column(modifier = Modifier.fillMaxSize().weight(1f)) {
              Text(
                  modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                  text = "Landing :",
                  textAlign = TextAlign.Left)
              Row {
                Text(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    text = flight.landingTime.format(DateTimeFormatter.ofPattern("HH:mm")))
                Text(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    text = flight.landingLocation.provider.toString())
              }
            }
          }
    }
  }
  Spacer(modifier = Modifier.padding(8.dp))
}

@Composable
fun FiltersMenu(
    onDismissRequest: () -> Unit,
    onConfirmRequest: (LocalDate?, LocalDate?, LocalTime?, LocalTime?, FlightType?) -> Unit,
) {
  var showRangeDatePicker by remember { mutableStateOf(false) }
  var beginDate: LocalDate? by remember { mutableStateOf(null) }
  var endDate: LocalDate? by remember { mutableStateOf(null) }
  var beginFlightTime: LocalTime? by remember { mutableStateOf(null) }
  var endFlightTime: LocalTime? by remember { mutableStateOf(null) }
  var flightType: FlightType? by remember { mutableStateOf(null) }
  AlertDialog(
      modifier = Modifier.fillMaxWidth().testTag("Filter Menu"),
      onDismissRequest = onDismissRequest,
      title = { Text("Filters") },
      text = {
        Column(Modifier.fillMaxWidth()) {
          if (showRangeDatePicker) {
            AlertDialog(
                modifier = Modifier.fillMaxWidth().testTag("Date Range Picker"),
                onDismissRequest = { showRangeDatePicker = false },
                title = { Text("Select date range") },
                text = {
                  DateRangeSelector { start, end ->
                    beginDate = start
                    endDate = end
                    showRangeDatePicker = false
                  }
                },
                confirmButton = {},
                dismissButton = {})
          } else {
            TitledRangedFilterField(
                title = "Date",
                minValue = beginDate,
                maxValue = endDate,
                onClick = { showRangeDatePicker = true },
                showString = { getFormattedDate(it) })
            TitledRangedFilterField(
                title = "Flight Time",
                minValue = beginFlightTime,
                maxValue = endFlightTime,
                onClick = { /* TODO time picker */},
                showString = { getFormattedTime(it) })
            TitledDropDownMenu(
                defaultPadding = 16.dp,
                title = "Flight type",
                value = flightType,
                onclickMenu = { type -> flightType = type },
                items = FlightType.ALL_FLIGHTS,
                showString = { it?.name ?: "" })
          }
        }
      },
      confirmButton = {
        TextButton(
            onClick = {
              onConfirmRequest(beginDate, endDate, beginFlightTime, endFlightTime, flightType)
            }) {
              Text("Apply")
            }
      },
      dismissButton = { TextButton(onClick = onDismissRequest) { Text("Cancel") } })
}

fun cardColors(flightType: FlightType): Color {
  return when (flightType) {
    FlightType.DISCOVERY -> veryLightBlue
    FlightType.PREMIUM -> veryLightYellow
    FlightType.FONDUE -> veryLightJasmine
    FlightType.HIGH_ALTITUDE -> veryLightSatin
    else -> veryLightRed
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeSelector(onDone: (LocalDate, LocalDate) -> Unit) {
  val state = rememberDateRangePickerState()
  var beginDate: LocalDate? = null
  var endDate: LocalDate? = null
  DateRangePicker(
      state,
      modifier = Modifier,
      dateFormatter = DatePickerFormatter("yy MM dd", "yy MM dd", "yy MM dd"),
      dateValidator = dateValidator(),
      title = {
        Text(text = "Select date range to assign the chart", modifier = Modifier.padding(16.dp))
      },
      headline = {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
          Box(Modifier.weight(1f)) {
            (if (state.selectedStartDateMillis != null)
                    state.selectedStartDateMillis?.let {
                      beginDate = dateToLocalDate(it)
                      getFormattedDate(beginDate!!)
                    }
                else "Start Date")
                ?.let { Text(text = it, style = MaterialTheme.typography.headlineSmall) }
          }
          Box(Modifier.weight(1f)) {
            (if (state.selectedEndDateMillis != null)
                    state.selectedEndDateMillis?.let {
                      endDate = dateToLocalDate(it)
                      getFormattedDate(dateToLocalDate(it))
                    }
                else "End Date")
                ?.let { Text(text = it, style = MaterialTheme.typography.headlineSmall) }
          }
          Box(Modifier.weight(0.2f)) {
            IconButton(onClick = { onDone(beginDate!!, endDate!!) }) {
              Icon(imageVector = Icons.Default.Done, contentDescription = "Ok")
            }
          }
        }
      },
      showModeToggle = false,
      colors =
          DatePickerDefaults.colors(
              containerColor = Color.Blue,
              titleContentColor = Color.Black,
              headlineContentColor = Color.Black,
              weekdayContentColor = Color.Black,
              subheadContentColor = Color.Black,
              yearContentColor = Color.Green,
              currentYearContentColor = Color.Red,
              selectedYearContainerColor = Color.Red,
              disabledDayContentColor = Color.Gray,
              todayDateBorderColor = Color.Blue,
              dayInSelectionRangeContainerColor = Color.LightGray,
              dayInSelectionRangeContentColor = Color.White,
              selectedDayContainerColor = Color.Black))
}

/** Let the DatePicker only pick dates that are in the past */
fun dateValidator(): (Long) -> Boolean {
  return { timeInMillis ->
    val date = dateToLocalDate(timeInMillis)
    date <= LocalDate.now()
  }
}

fun dateToLocalDate(date: Long): LocalDate {
  return Instant.ofEpochMilli(date).atZone(ZoneId.of("GMT")).toLocalDate()
}

fun getFormattedDate(date: LocalDate?): String {
  return date?.format(DateTimeFormatter.ofPattern("dd/MM/yy")) ?: "--/--/--"
}

fun getFormattedTime(time: LocalTime?): String {
  return time?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "--:--"
}

/**
 * Search bar for flights with the location of the flight as filter
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchBar(modifier: Modifier, onSearch: (String) -> Unit, results: List<Flight>) {
  var query by remember { mutableStateOf("") }
  var active by rememberSaveable { mutableStateOf(false) }
  SearchBar(
      modifier = modifier,
      query = query,
      onQueryChange = { query = it },
      onSearch = onSearch,
      active = active,
      onActiveChange = { active = it },
      placeholder = { Text(" Search for a location") },
      trailingIcon = {
        if (active) {
          IconButton(onClick = { query = "" }) {
            Icon(Icons.Filled.Clear, contentDescription = "Clear search")
          }
        } else {
          IconButton(onClick = { active = true }) {
            Icon(Icons.Filled.Search, contentDescription = "Search")
          }
        }
      }) {
        LazyColumn {
          items(results) { flight -> ListItem(headlineContent = { Text(flight.date.toString()) }) }
        }
      }
}

@Composable
fun <T> TitledRangedFilterField(
    title: String,
    minValue: T,
    maxValue: T,
    onClick: () -> Unit,
    showString: (T) -> String
) {
  Text(
      modifier = Modifier.fillMaxWidth().padding(5.dp),
      text = "$title range : ",
      style = MaterialTheme.typography.headlineSmall)
  Row {
    OutlinedTextField(
        modifier =
            Modifier.clickable { onClick() }
                .fillMaxWidth(0.9f)
                .weight(1f)
                .testTag("$title Range Field 1"),
        value = showString(minValue),
        onValueChange = {},
        readOnly = true,
        enabled = false)
    Text(
        modifier = Modifier.padding(vertical = 10.dp, horizontal = 5.dp),
        text = " to ",
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center)
    OutlinedTextField(
        modifier =
            Modifier.clickable { onClick() }
                .fillMaxWidth(0.9f)
                .weight(1f)
                .testTag("$title Range Field 2"),
        value = showString(maxValue),
        onValueChange = {},
        readOnly = true,
        enabled = false)
  }
}

@Preview
@Composable
fun FlightHistoryScreenPreview() {
  val allFlights: MutableList<FinishedFlight> = remember {
    mutableStateListOf(
        FinishedFlight(
            id = UNSET_ID,
            nPassengers = 0,
            team = Team(Role.initRoles(BASE_ROLES)),
            flightType = FlightType.DISCOVERY,
            balloon = Balloon("Balloon", BalloonQualification.MEDIUM),
            basket = Basket("Basket", true),
            date = LocalDate.now(),
            timeSlot = TimeSlot.AM,
            vehicles = emptyList(),
            flightTime = 0L,
            takeOffTime = LocalTime.now(),
            landingTime = LocalTime.now(),
            takeOffLocation = Location("Lausanne"),
            landingLocation = Location("Lausanne")),
        FinishedFlight(
            id = UNSET_ID,
            nPassengers = 0,
            team = Team(Role.initRoles(BASE_ROLES)),
            flightType = FlightType.HIGH_ALTITUDE,
            balloon = Balloon("Balloon", BalloonQualification.MEDIUM),
            basket = Basket("Basket", true),
            date = LocalDate.now(),
            timeSlot = TimeSlot.AM,
            vehicles = emptyList(),
            flightTime = 0L,
            takeOffTime = LocalTime.now(),
            landingTime = LocalTime.now(),
            takeOffLocation = Location("Lausanne"),
            landingLocation = Location("Lausanne")))
  }
  FlightHistoryScreen(navController = rememberNavController(), allFlights = allFlights)
}
