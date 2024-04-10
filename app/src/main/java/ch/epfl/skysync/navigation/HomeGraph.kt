package ch.epfl.skysync.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ch.epfl.skysync.screens.AddFlight
import ch.epfl.skysync.screens.ChatScreen
import ch.epfl.skysync.screens.FlightScreen
import ch.epfl.skysync.screens.HomeScreen
import ch.epfl.skysync.screens.ShowFlightCalendar
import ch.epfl.skysync.screens.showCalendarAvailabilities

/** Graph of the main screens of the app */
fun NavGraphBuilder.homeGraph(navController: NavHostController) {
  navigation(startDestination = Route.HOME, route = Route.MAIN) {
    composable(Route.CALENDAR) { showCalendarAvailabilities() }
    composable(Route.CHAT) { ChatScreen(navController) }
    composable(Route.FLIGHT) { FlightScreen(navController) }
    composable(Route.HOME) { HomeScreen(navController) }
    composable(Route.FLIGHTCALENDAR) { ShowFlightCalendar(navController) }
    composable(Route.ADDFLIGHT) { AddFlight(navController) }
  }
}
