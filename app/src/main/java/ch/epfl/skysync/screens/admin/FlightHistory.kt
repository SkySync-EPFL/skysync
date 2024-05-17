package ch.epfl.skysync.screens.admin

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ch.epfl.skysync.R
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.components.forms.TitledDropDownMenu
import ch.epfl.skysync.database.DateUtility.dateToHourMinuteString
import ch.epfl.skysync.database.DateUtility.dateToLocalDate
import ch.epfl.skysync.database.DateUtility.localDateToString
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.navigation.AdminBottomBar
import ch.epfl.skysync.ui.theme.veryLightBlue
import ch.epfl.skysync.ui.theme.veryLightJasmine
import ch.epfl.skysync.ui.theme.veryLightRed
import ch.epfl.skysync.ui.theme.veryLightSatin
import ch.epfl.skysync.ui.theme.veryLightYellow
import ch.epfl.skysync.viewmodel.FinishedFlightsViewModel
import java.time.LocalDate
import java.util.Date

@Composable
fun FlightHistoryScreen(
    navController: NavHostController,
    finishedFlightsViewModel: FinishedFlightsViewModel
) {

  Scaffold(
      topBar = { CustomTopAppBar(navController = navController, title = "Flight History") },
      bottomBar = { AdminBottomBar(navController = navController) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
          val allFlights = finishedFlightsViewModel.currentFlights.collectAsState()
          if (allFlights.value == null) {
            LoadingComponent(
                isLoading = true, onRefresh = { finishedFlightsViewModel.refresh() }) {}
          } else {
            if (allFlights.value!!.isEmpty()) {
              Text(
                  modifier = Modifier.padding(padding).testTag("No Flight"),
                  text = "No flights available")
            } else {
              var beginDate: LocalDate? by remember { mutableStateOf(null) }
              var endDate: LocalDate? by remember { mutableStateOf(null) }
              var beginFlightTime: Date? by remember { mutableStateOf(null) }
              var endFlightTime: Date? by remember { mutableStateOf(null) }
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
              Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                FlightSearchBar(
                    modifier = Modifier.fillMaxWidth().weight(3f),
                    onSearch = { /* TODO search for flights by location name */},
                    results = allFlights.value!!)
                IconButton(
                    modifier = Modifier.testTag("Filter Button"),
                    onClick = { showFilters = !showFilters },
                    content = {
                      Icon(
                          modifier = Modifier.padding(top = 16.dp),
                          painter = painterResource(id = R.drawable.baseline_filter_list_alt_24),
                          contentDescription = "Filters")
                    })
              }
            }
            LazyColumn() {
              itemsIndexed(allFlights.value!!) { id, flight ->
                HistoryCard(flight, Modifier.testTag("Card $id"))
              }
            }
          }
        }
      }
}

/**
 * Representation of a finished flight
 *
 * @param flight flight to be displayed
 * @param modifier modifier for the card (there for the test tag)
 */
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
            text = localDateToString(flight.date),
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
                    text = dateToHourMinuteString(flight.takeOffTime))
                Text(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    text =
                        flight.takeOffLocation.name.ifEmpty {
                          flight.takeOffLocation.latlng().toString()
                        })
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
                    text = dateToHourMinuteString(flight.landingTime))
                Text(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    text =
                        flight.landingLocation.name.ifEmpty {
                          flight.landingLocation.latlng().toString()
                        })
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
    onConfirmRequest: (LocalDate?, LocalDate?, Date?, Date?, FlightType?) -> Unit,
) {
  var showRangeDatePicker by remember { mutableStateOf(false) }
  var beginDate: LocalDate? by remember { mutableStateOf(null) }
  var endDate: LocalDate? by remember { mutableStateOf(null) }
  var beginFlightTime: Date? by remember { mutableStateOf(null) }
  var endFlightTime: Date? by remember { mutableStateOf(null) }
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
                showString = { localDateToString(it) })
            TitledRangedFilterField(
                title = "Flight Time",
                minValue = beginFlightTime,
                maxValue = endFlightTime,
                onClick = { /* TODO time picker */},
                showString = { dateToHourMinuteString(it) })
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
      modifier = Modifier.testTag("Date Range Selector"),
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
                      localDateToString(beginDate!!)
                    }
                else "Start Date")
                ?.let { Text(text = it, style = MaterialTheme.typography.headlineSmall) }
          }
          Box(Modifier.weight(1f)) {
            (if (state.selectedEndDateMillis != null)
                    state.selectedEndDateMillis?.let {
                      endDate = dateToLocalDate(it)
                      localDateToString(dateToLocalDate(it))
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

/** Search bar for flights with the location of the flight as filter */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchBar(modifier: Modifier, onSearch: (String) -> Unit, results: List<FinishedFlight>) {
  var query by remember { mutableStateOf("") }
  var active by rememberSaveable { mutableStateOf(false) }
  SearchBar(
      modifier = modifier.testTag("Search Bar"),
      query = query,
      onQueryChange = { query = it },
      onSearch = {
        onSearch(it)
        active = false
      },
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
          items(results) { flight ->
            ListItem(
                headlineContent = {
                  Text(
                      "take off : ${flight.takeOffLocation.name.ifEmpty { flight.takeOffLocation.latlng().toString()}}")
                  Text(
                      "landing : ${flight.landingLocation.name.ifEmpty { flight.landingLocation.latlng().toString()}}")
                })
          }
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
