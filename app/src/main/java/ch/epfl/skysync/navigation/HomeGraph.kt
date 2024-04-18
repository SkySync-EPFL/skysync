package ch.epfl.skysync.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.screens.AddFlightScreen
import ch.epfl.skysync.screens.ChatScreen
import ch.epfl.skysync.screens.FlightScreen
import ch.epfl.skysync.screens.HomeScreen
import ch.epfl.skysync.screens.flightDetail.FlightDetailScreen
import ch.epfl.skysync.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseUser
import java.time.LocalDate

/** Graph of the main screens of the app */
fun NavGraphBuilder.homeGraph(navController: NavHostController, user: FirebaseUser?) {
  // Only there for preview purposes. It will then be integrated in a model view
  val listFlights =
      mutableListOf(
          PlannedFlight(
              nPassengers = 4,
              date = LocalDate.of(2024, 3, 19),
              timeSlot = TimeSlot.AM,
              flightType = FlightType.FONDUE,
              vehicles = listOf(),
              balloon = null,
              basket = null,
              id = 1.toString()),
          PlannedFlight(
              nPassengers = 2,
              date = LocalDate.of(2024, 3, 20),
              timeSlot = TimeSlot.AM,
              flightType = FlightType.DISCOVERY,
              vehicles = listOf(),
              balloon = null,
              basket = null,
              id = 2.toString()),
          PlannedFlight(
              nPassengers = 3,
              date = LocalDate.of(2024, 3, 22),
              timeSlot = TimeSlot.PM,
              flightType = FlightType.DISCOVERY,
              vehicles = listOf(),
              balloon = null,
              basket = null,
              id = 3.toString()),
      )
  navigation(startDestination = Route.HOME, route = Route.MAIN) {
    personalCalendar(navController, user)
    composable(Route.CHAT) { ChatScreen(navController) }
    composable(Route.FLIGHT) { FlightScreen(navController) }
    composable(Route.HOME) { HomeScreen(navController, listFlights) }
    composable(
        Route.FLIGHT_DETAILS + "/{Flight ID}",
        arguments = listOf(navArgument("Flight ID") { type = NavType.StringType })) { backStackEntry
          ->
          val flightId = backStackEntry.arguments?.getString("Flight ID") ?: UNSET_ID
          val viewModel = UserViewModel.createViewModel(user)
          FlightDetailScreen(
              navController = navController, flightId = flightId, viewModel = viewModel)
        }
    composable(Route.ADD_FLIGHT) { AddFlightScreen(navController, listFlights) }
  }
}
