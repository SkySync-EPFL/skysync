package ch.epfl.skysync.screens.admin

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import ch.epfl.skysync.components.AvailabilityCalendar
import ch.epfl.skysync.components.FlightCalendar
import ch.epfl.skysync.navigation.AdminBottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.screens.crewpilot.CalendarTopBar
import ch.epfl.skysync.viewmodel.CalendarViewModel

@Composable
fun AdminCalendarScreen(
    navController: NavHostController,
    calendarType: String,
    viewModel: CalendarViewModel
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
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        if (calendarType == Route.ADMIN_AVAILABILITY_CALENDAR) {
          val availabilityCalendar = uiState.availabilityCalendar
          AvailabilityCalendar(
              padding = padding,
              getAvailabilityStatus = { date, time ->
                availabilityCalendar.getAvailabilityStatus(date, time)
              },
              nextAvailabilityStatus = { date, time ->
                availabilityCalendar.nextAvailabilityStatus(date, time)
              },
              onSave = { viewModel.saveAvailabilities() },
              onCancel = { Log.d("TO BE IMPLEMENTED", "Cancel in Availabilities") })
        } else if (calendarType == Route.ADMIN_FLIGHT_CALENDAR) {
          val flightCalendar = uiState.flightGroupCalendar
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
