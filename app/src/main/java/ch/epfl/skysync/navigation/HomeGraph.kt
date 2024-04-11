package ch.epfl.skysync.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ch.epfl.skysync.screens.ChatScreen
import ch.epfl.skysync.screens.FlightScreen
import ch.epfl.skysync.screens.HomeScreen
import com.google.firebase.auth.FirebaseUser

/** Graph of the main screens of the app */
fun NavGraphBuilder.homeGraph(navController: NavHostController, user: FirebaseUser?) {
  navigation(startDestination = Route.HOME, route = Route.MAIN) {
    personalCalendar(navController, user)
    composable(Route.CHAT) { ChatScreen(navController) }
    composable(Route.FLIGHT) { FlightScreen(navController) }
    composable(Route.HOME) { HomeScreen(navController) }
  }
}
