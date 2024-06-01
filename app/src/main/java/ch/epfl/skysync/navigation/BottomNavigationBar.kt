package ch.epfl.skysync.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * The bottom navigation bar for the crew/pilot user
 *
 * @param navController The navigation controller
 */
@Composable
fun BottomBar(navController: NavHostController) {
  val screens =
      listOf(
          BottomBarScreen.Home,
          BottomBarScreen.Flight,
          BottomBarScreen.Chat,
          BottomBarScreen.Calendar,
          BottomBarScreen.Stats)
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentDestination = navBackStackEntry?.destination

  NavigationBar {
    screens.forEach { screen ->
      AddItem(
          screen = screen, currentDestination = currentDestination, navController = navController)
    }
  }
}
