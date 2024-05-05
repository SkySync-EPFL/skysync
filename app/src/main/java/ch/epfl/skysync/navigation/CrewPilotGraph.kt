package ch.epfl.skysync.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import ch.epfl.skysync.Repository
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.screens.crewpilot.ChatScreen
import ch.epfl.skysync.screens.crewpilot.FlightDetailScreen
import ch.epfl.skysync.screens.crewpilot.FlightScreen
import ch.epfl.skysync.screens.crewpilot.HomeScreen
import ch.epfl.skysync.screens.crewpilot.TextScreen
import ch.epfl.skysync.viewmodel.ChatViewModel
import ch.epfl.skysync.viewmodel.FlightsViewModel
import ch.epfl.skysync.viewmodel.LocationViewModel
import ch.epfl.skysync.viewmodel.MessageListenerSharedViewModel
import ch.epfl.skysync.viewmodel.TimerViewModel

fun NavGraphBuilder.crewPilotGraph(
    repository: Repository,
    navController: NavHostController,
    uid: String?,
    timer: TimerViewModel? = null
) {
    navigation(startDestination = Route.HOME, route = Route.CREW_PILOT) {
        personalCalendar(repository, navController, uid)
        composable(
            route = Route.FLIGHT_DETAILS + "/{Flight ID}",
            arguments = listOf(navArgument("Flight ID") { type = NavType.StringType })
        ) { backStackEntry
            ->
            val flightId = backStackEntry.arguments?.getString("Flight ID") ?: UNSET_ID
            val flightsViewModel = FlightsViewModel.createViewModel(repository, uid)
            FlightDetailScreen(
                navController = navController, flightId = flightId, viewModel = flightsViewModel
            )
        }
        composable(
            Route.TEXT + "/{Group ID}",
            arguments = listOf(navArgument("Group ID") { type = NavType.StringType })
        ) { entry ->
            val messageListenerSharedViewModel =
                entry.sharedViewModel<MessageListenerSharedViewModel>(
                    navController,
                )

            val chatViewModel =
                ChatViewModel.createViewModel(uid!!, messageListenerSharedViewModel, repository)
            val groupId = entry.arguments?.getString("Group ID")
            if (groupId == null) {
                navController.navigate(Route.HOME)
                return@composable
            }
            TextScreen(navController, groupId, chatViewModel)
        }
        composable(Route.HOME) { entry ->

            // get the MessageListenerSharedViewModel here so that it gets
            // instantiated
            val messageListenerSharedViewModel =
                entry.sharedViewModel<MessageListenerSharedViewModel>(
                    navController,
                )
            messageListenerSharedViewModel.init(uid!!, repository) { group, update ->
                onMessageUpdate(group, update)
            }

            val flightsOverviewViewModel = FlightsViewModel.createViewModel(repository, uid)
            flightsOverviewViewModel.refresh()
            HomeScreen(navController, flightsOverviewViewModel)
        }
        composable(Route.CHAT) { entry ->
            val messageListenerSharedViewModel =
                entry.sharedViewModel<MessageListenerSharedViewModel>(
                    navController,
                )
            val chatViewModel =
                ChatViewModel.createViewModel(uid!!, messageListenerSharedViewModel, repository)
            ChatScreen(navController, chatViewModel)
        }
        composable(Route.FLIGHT) {
            val locationViewModel = LocationViewModel.createViewModel(repository)
            FlightScreen(navController, timer!!, locationViewModel, uid!!)
        }
    }
}