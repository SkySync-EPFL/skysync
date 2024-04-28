package ch.epfl.skysync.navigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import ch.epfl.skysync.R

@Composable
fun AdminBottomBar(navController: NavHostController) {
  val screens =
      listOf(
          BottomBarScreen.Home,
          BottomBarScreen.Flight,
          BottomBarScreen.Chat,
          BottomBarScreen.Calendar,
      )
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentDestination = navBackStackEntry?.destination

  NavigationBar {
    screens.forEach { screen ->
      AddItem(
          screen = screen, currentDestination = currentDestination, navController = navController)
    }
  }
}
