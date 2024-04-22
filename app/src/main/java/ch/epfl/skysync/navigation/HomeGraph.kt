package ch.epfl.skysync.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ch.epfl.skysync.Repository
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.screens.AddFlightScreen
import ch.epfl.skysync.screens.ChatScreen
import ch.epfl.skysync.screens.FlightScreen
import ch.epfl.skysync.screens.HomeScreen
import ch.epfl.skysync.screens.ModifyFlightScreen
import ch.epfl.skysync.viewmodel.FlightsViewModel
import java.time.LocalDate

/** Graph of the main screens of the app */
fun NavGraphBuilder.homeGraph(
    repository: Repository,
    navController: NavHostController,
    flightsViewModel: FlightsViewModel,
    uid: String?
) {
  navigation(startDestination = Route.HOME, route = Route.MAIN) {
    personalCalendar(repository, navController, uid)
    composable(Route.CHAT) { ChatScreen(navController) }
    composable(Route.FLIGHT) { FlightScreen(navController) }
    composable(Route.HOME) { HomeScreen(navController, flightsViewModel) }
    composable(Route.ADD_FLIGHT) { AddFlightScreen(navController, flightsViewModel) }
    composable(Route.MODIFY_FLIGHT) {
      ModifyFlightScreen(
          navController,
          flightsViewModel,
          PlannedFlight(
              id = UNSET_ID,
              nPassengers = 2,
              flightType = FlightType.DISCOVERY,
              timeSlot = TimeSlot.AM,
              date = LocalDate.now(),
              vehicles = emptyList(),
              balloon = null,
              basket = null,
              team = Team(roles = emptyList())))
    }
  }
}
