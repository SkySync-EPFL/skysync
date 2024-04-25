package ch.epfl.skysync.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
import ch.epfl.skysync.screens.TextScreen
import ch.epfl.skysync.screens.confirmationScreen
import ch.epfl.skysync.screens.flightDetail.FlightDetailScreen
import ch.epfl.skysync.viewmodel.FlightsViewModel
import java.time.LocalDate

/** Graph of the main screens of the app */
fun NavGraphBuilder.homeGraph(
    repository: Repository,
    navController: NavHostController,
    uid: String?
) {
  navigation(startDestination = Route.HOME, route = Route.MAIN) {
    personalCalendar(repository, navController, uid)
    composable(Route.CHAT) { ChatScreen(navController) }
    composable(Route.FLIGHT) { FlightScreen(navController) }
    composable(Route.HOME) {
      val flightsViewModel =
          FlightsViewModel.createViewModel(
              flightTable = repository.flightTable,
              balloonTable = repository.balloonTable,
              basketTable = repository.basketTable,
              flightTypeTable = repository.flightTypeTable,
              vehicleTable = repository.vehicleTable)
      HomeScreen(navController, flightsViewModel)
    }
    composable(
        Route.FLIGHT_DETAILS + "/{Flight ID}",
        arguments = listOf(navArgument("Flight ID") { type = NavType.StringType })) { backStackEntry
          ->
          val flightId = backStackEntry.arguments?.getString("Flight ID") ?: UNSET_ID
          //      val flightsViewModel =
          //      FlightsViewModel.createViewModel(
          //          flightTable = repository.flightTable,
          //          balloonTable = repository.balloonTable,
          //          basketTable = repository.basketTable,
          //          flightTypeTable = repository.flightTypeTable,
          //          vehicleTable = repository.vehicleTable)
          FlightDetailScreen(
              navController = navController,
              flightId = flightId,
              // viewModel = flightsViewModel
          )
        }
    composable(Route.ADD_FLIGHT) {
      val flightsViewModel =
          FlightsViewModel.createViewModel(
              flightTable = repository.flightTable,
              balloonTable = repository.balloonTable,
              basketTable = repository.basketTable,
              flightTypeTable = repository.flightTypeTable,
              vehicleTable = repository.vehicleTable)
      AddFlightScreen(navController, flightsViewModel)
    }
    composable(
        Route.CONFIRM_FLIGHT + "/{Flight ID}",
        arguments = listOf(navArgument("Flight ID") { type = NavType.StringType })) { backStackEntry
          ->
          val flightId = backStackEntry.arguments?.getString("Flight ID") ?: UNSET_ID
          val flightsViewModel =
              FlightsViewModel.createViewModel(
                  flightTable = repository.flightTable,
                  balloonTable = repository.balloonTable,
                  basketTable = repository.basketTable,
                  flightTypeTable = repository.flightTypeTable,
                  vehicleTable = repository.vehicleTable)
          confirmationScreen(navController, flightId, flightsViewModel)
        }
    composable(Route.MODIFY_FLIGHT) {
      val flightsViewModel =
          FlightsViewModel.createViewModel(
              flightTable = repository.flightTable,
              balloonTable = repository.balloonTable,
              basketTable = repository.basketTable,
              flightTypeTable = repository.flightTypeTable,
              vehicleTable = repository.vehicleTable)
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
    composable(
        Route.TEXT + "/{Group Name}",
        arguments = listOf(navArgument("Group Name") { type = NavType.StringType })) {
            backStackEntry ->
          val groupName = backStackEntry.arguments?.getString("Group Name") ?: "No Name"
          TextScreen(navController, groupName)
        }
  }
}
