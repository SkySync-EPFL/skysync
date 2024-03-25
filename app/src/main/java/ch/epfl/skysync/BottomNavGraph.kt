package ch.epfl.skysync

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ch.epfl.skysync.screens.CalendarScreen
import ch.epfl.skysync.screens.ChatScreen
import ch.epfl.skysync.screens.FlightScreen
import ch.epfl.skysync.screens.HomeScreen


@Composable
fun BottomNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Home.route
    ) {
        composable(route = BottomBarScreen.Home.route) {
            HomeScreen()
        }
        composable(route = BottomBarScreen.Flight.route) {
            FlightScreen()
        }
        composable(route = BottomBarScreen.Chat.route) {
            ChatScreen()
        }
        composable(route = BottomBarScreen.Calendar.route) {
            CalendarScreen()
        }
    }
}