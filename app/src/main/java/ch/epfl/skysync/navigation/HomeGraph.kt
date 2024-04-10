package ch.epfl.skysync.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ch.epfl.skysync.screens.CalendarScreen
import ch.epfl.skysync.screens.ChatScreen
import ch.epfl.skysync.screens.FlightScreen
import ch.epfl.skysync.screens.HomeScreen
import ch.epfl.skysync.viewmodel.UserViewModel
import com.firebase.ui.auth.data.model.User
import com.google.firebase.auth.FirebaseUser

/** Graph of the main screens of the app */
fun NavGraphBuilder.homeGraph(
  navController: NavHostController,
  user: FirebaseUser?
) {
  navigation(
    startDestination = Route.HOME,
    route = Route.MAIN
  )
  {
    personalCalendar(navController, user)
    composable(Route.CHAT) {
      ChatScreen(navController) }
    composable(Route.FLIGHT) {
      FlightScreen(navController)
    }
    composable(Route.HOME) {
      HomeScreen(navController)
    }
  }
}




