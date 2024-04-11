package ch.epfl.skysync.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.screens.CalendarScreen
import ch.epfl.skysync.screens.ChatScreen
import ch.epfl.skysync.screens.FlightScreen
import ch.epfl.skysync.screens.HomeScreen

/** Graph of the main screens of the app */
fun NavGraphBuilder.homeGraph(navController: NavHostController) {
  val flights = mutableListOf(emptyList<Flight>())
  navigation(startDestination = Route.HOME, route = Route.MAIN) {
    composable(Route.CALENDAR) { CalendarScreen(navController) }
    composable(Route.CHAT) { ChatScreen(navController) }
    composable(Route.FLIGHT) { FlightScreen(navController) }
    composable(Route.HOME) { HomeScreen(navController) }
  }
}
