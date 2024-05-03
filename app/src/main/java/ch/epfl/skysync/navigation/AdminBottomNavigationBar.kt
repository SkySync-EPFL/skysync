package ch.epfl.skysync.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun AdminBottomBar(navController: NavHostController) {
  val screens =
      listOf(
          BottomBarScreen.Home,
          BottomBarScreen.User,
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

@Preview
@Composable
fun PreviewAdminBottomBar() {
  AdminBottomBar(navController = rememberNavController())
}
