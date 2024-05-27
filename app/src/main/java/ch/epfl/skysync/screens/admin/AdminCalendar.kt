package ch.epfl.skysync.screens.admin

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import ch.epfl.skysync.components.AvailabilityCalendar
import ch.epfl.skysync.components.ConnectivityStatus
import ch.epfl.skysync.components.FlightCalendar
import ch.epfl.skysync.navigation.AdminBottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.screens.crewpilot.CalendarTopBar
import ch.epfl.skysync.viewmodel.CalendarViewModel

@Composable
fun AdminCalendarScreen(
    navController: NavHostController,
    calendarType: String,
    viewModel: CalendarViewModel,
    connectivityStatus: ConnectivityStatus
) {
  val tabs = mapOf(Route.ADMIN_FLIGHT_CALENDAR to 0, Route.ADMIN_AVAILABILITY_CALENDAR to 1)
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
      bottomBar = { AdminBottomBar(navController) }) { padding ->
        if (calendarType == Route.ADMIN_AVAILABILITY_CALENDAR) {
          val availabilityCalendar by
              viewModel.currentAvailabilityCalendar.collectAsStateWithLifecycle()
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
        } else if (calendarType == Route.ADMIN_FLIGHT_CALENDAR) {
          val flightCalendar by viewModel.currentFlightGroupCalendar.collectAsStateWithLifecycle()
          FlightCalendar(
              padding = padding,
              getFirstFlightByDate = { date, time ->
                flightCalendar.getFirstFlightByDate(date, time)
              },
              onFlightClick = { selectedFlight ->
                navController.navigate(Route.ADMIN_FLIGHT_DETAILS + "/${selectedFlight}")
              })
        }
      }
}
