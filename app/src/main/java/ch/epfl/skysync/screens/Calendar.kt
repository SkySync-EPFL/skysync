package ch.epfl.skysync.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.AvailabilityCalendar
import ch.epfl.skysync.components.FlightCalendar
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.viewmodel.CalendarViewModel

@Composable
fun CalendarScreen(
    navController: NavHostController,
    calendarType: String,
    viewModel: CalendarViewModel
) {
  Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = { BottomBar(navController) }) { padding ->
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (calendarType == Route.AVAILABILITY_CALENDAR) {
      val availabilityCalendar = uiState.availabilityCalendar
      AvailabilityCalendar(
          padding,
          onFlightCalendarClick = { navController.navigate(Route.PERSONAL_FLIGHT_CALENDAR) },
          getAvailabilityStatus = { date, time ->
            availabilityCalendar?.getAvailabilityStatus(date, time) ?: AvailabilityStatus.UNDEFINED
          },
          nextAvailabilityStatus = { date, time ->
            availabilityCalendar?.nextAvailabilityStatus(date, time) ?: AvailabilityStatus.UNDEFINED
          },
          onSave = { viewModel.saveAvailabilities() })
    } else if (calendarType == Route.PERSONAL_FLIGHT_CALENDAR) {
      val flightCalendar = uiState.flightGroupCalendar
      FlightCalendar(
          padding,
          onAvailabilityCalendarClick = { navController.navigate(Route.AVAILABILITY_CALENDAR) },
          getFirstFlightByDate = { date, time -> flightCalendar?.getFirstFlightByDate(date, time) },
          onFlightClick = {
            // TODO: navigate to flight details screen
          })
    }
  }
}

/*
 fun onClickT(tabIndex: Int, navController: NavHostController) { when (tabIndex) { 0 ->
 navController.navigate(Route.PERSONAL_FLIGHT_CALENDAR) 1 ->
 navController.navigate(Route.AVAILABILITY_CALENDAR) } }

 @Composable fun CalendarScreenNew(navController: NavHostController, tab: String, viewModel:
   CalendarViewModel)
 { val tabs = mapOf(Route.FLIGHT_CALENDAR to 0, Route.AVAILABILITY_CALENDAR
   to 1)
     val tabIndex = tabs[tab]
     val uiState by viewModel.uiState.collectAsStateWithLifecycle()
   Scaffold( modifier = Modifier.fillMaxSize(), topBar = { tabIndex?.let { TopBar(tabIndex = it,
   tabs = tabs, onclick = { it -> onClickT(it, navController) }) } }, bottomBar = {
   BottomBar(navController) }) { padding -> if (tabIndex == 1) { val availabilityCalendar =
   uiState.availabilityCalendar Column(modifier = Modifier.padding(padding)) {
   AvailabilityCalendarNew( padding, getAvailabilityStatus = { date, time ->
   availabilityCalendar?.getAvailabilityStatus(date, time) ?: AvailabilityStatus.UNDEFINED },
   nextAvailabilityStatus = { date, time -> availabilityCalendar?.nextAvailabilityStatus(date,
   time) ?: AvailabilityStatus.UNDEFINED }, onSave = { viewModel.saveAvailabilities() }) } } else
   if (tabIndex == 0) { val flightCalendar = uiState.flightGroupCalendar FlightCalendar( padding,
   onAvailabilityCalendarClick = { navController.navigate(Route.AVAILABILITY_CALENDAR) },
   getFirstFlightByDate = { date, time -> flightCalendar?.getFirstFlightByDate(date, time) },
   onFlightClick = { // TODO: navigate to flight details screen }) } } }
 @Composable fun TopBar(tabIndex: Int, tabs: Map<String, Int>, onclick: (Int) -> Unit) {
   TabRow(selectedTabIndex = tabIndex) { tabs.forEach { (key, value) -> Tab(text = { Text(key) },
   selected = tabIndex == value, onClick = { onclick(value) }) } } }


*/
