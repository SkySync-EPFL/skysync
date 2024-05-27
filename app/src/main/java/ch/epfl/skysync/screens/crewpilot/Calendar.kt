package ch.epfl.skysync.screens.crewpilot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import ch.epfl.skysync.components.AvailabilityCalendar
import ch.epfl.skysync.components.ConnectivityStatus
import ch.epfl.skysync.components.FlightCalendar
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.lightGray
import ch.epfl.skysync.viewmodel.CalendarViewModel

@Composable
fun CalendarScreen(
    navController: NavHostController,
    calendarType: String,
    viewModel: CalendarViewModel,
    connectivityStatus: ConnectivityStatus
) {
  val tabs = mapOf(Route.CREW_FLIGHT_CALENDAR to 0, Route.CREW_AVAILABILITY_CALENDAR to 1)
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentDestination = navBackStackEntry?.destination

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        CalendarTopBar(tab = calendarType, tabs = tabs) { route ->
          navController.navigate(route) {
            if (currentDestination?.route != route) {
              navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
              }
            }
          }
        }
      },
      bottomBar = { BottomBar(navController) }) { padding ->
        val availabilityCalendar by
            viewModel.currentAvailabilityCalendar.collectAsStateWithLifecycle()
        val flightGroupCalendar by
            viewModel.currentFlightGroupCalendar.collectAsStateWithLifecycle()
        if (calendarType == Route.CREW_AVAILABILITY_CALENDAR) {
          AvailabilityCalendar(
              padding = padding,
              getAvailabilityStatus = { date, time ->
                availabilityCalendar.getAvailabilityStatus(date, time)
              },
              nextAvailabilityStatus = { date, time ->
                viewModel.setToNextAvailabilityStatus(date, time)
              },
              onSave = { viewModel.saveAvailabilities() },
              onCancel = { viewModel.cancelAvailabilities() },
              connectivityStatus)
        } else if (calendarType == Route.CREW_FLIGHT_CALENDAR) {
          FlightCalendar(
              padding = padding,
              getFirstFlightByDate = { date, time ->
                flightGroupCalendar.getFirstFlightByDate(date, time)
              },
              onFlightClick = { selectedFlight ->
                navController.navigate(Route.CREW_FLIGHT_DETAILS + "/${selectedFlight}")
              })
        }
      }
}

@Composable
fun CalendarTopBar(tab: String, tabs: Map<String, Int>, onclick: (String) -> Unit) {
  val tabIndex = tabs[tab]
  if (tabIndex != null) {
    TabRow(selectedTabIndex = tabIndex, containerColor = lightGray) {
      tabs.forEach { (route, index) ->
        Tab(
            modifier = Modifier.padding(8.dp).testTag(route),
            text = { Text(text = route) },
            selected = tabIndex == index,
            onClick = { onclick(route) })
      }
    }
  }
}
