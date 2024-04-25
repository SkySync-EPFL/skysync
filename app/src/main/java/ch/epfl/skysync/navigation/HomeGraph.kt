package ch.epfl.skysync.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import ch.epfl.skysync.Repository
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.screens.AddFlightScreen
import ch.epfl.skysync.screens.ChatScreen
import ch.epfl.skysync.screens.FlightScreen
import ch.epfl.skysync.screens.HomeScreen
import ch.epfl.skysync.screens.ModifyFlightScreen
import ch.epfl.skysync.screens.TextScreen
import ch.epfl.skysync.screens.confirmationScreen
import ch.epfl.skysync.screens.flightDetail.FlightDetailScreen
import ch.epfl.skysync.viewmodel.FlightsViewModel

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
      val flightsViewModel = FlightsViewModel.createViewModel(repository)
      flightsViewModel.refresh()
      HomeScreen(navController, flightsViewModel)
    }
    composable(
        route = Route.FLIGHT_DETAILS + "/{Flight ID}",
        arguments = listOf(navArgument("Flight ID") { type = NavType.StringType })) { backStackEntry
          ->
          val flightId = backStackEntry.arguments?.getString("Flight ID") ?: UNSET_ID
          val flightsViewModel = FlightsViewModel.createViewModel(repository)
          FlightDetailScreen(
              navController = navController, flightId = flightId, viewModel = flightsViewModel)
        }

    composable(Route.ADD_FLIGHT) {
      val flightsViewModel = FlightsViewModel.createViewModel(repository)
      AddFlightScreen(navController, flightsViewModel)
    }

    composable(
        Route.CONFIRM_FLIGHT + "/{Flight ID}",
        arguments = listOf(navArgument("Flight ID") { type = NavType.StringType })) { backStackEntry
          ->
          val flightId = backStackEntry.arguments?.getString("Flight ID") ?: UNSET_ID
          val flightsViewModel = FlightsViewModel.createViewModel(repository)
          confirmationScreen(navController, flightId, flightsViewModel)
        }

    composable(
        route = Route.MODIFY_FLIGHT + "/{Flight ID}",
        arguments = listOf(navArgument("Flight ID") { type = NavType.StringType })) { backStackEntry
          ->
          val flightId = backStackEntry.arguments?.getString("Flight ID") ?: UNSET_ID
          val flightsViewModel = FlightsViewModel.createViewModel(repository)
          ModifyFlightScreen(navController, flightsViewModel, flightId)
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
