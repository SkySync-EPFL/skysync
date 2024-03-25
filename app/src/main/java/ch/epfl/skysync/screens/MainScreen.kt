package ch.epfl.skysync.screens

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.BottomBarScreen
import ch.epfl.skysync.BottomNavGraph

@Composable
fun MainScreen() {
  val navController = rememberNavController()
  Scaffold(bottomBar = { BottomBar(navController = navController) }) {
    BottomNavGraph(navController = navController)
  }
}

@Composable
fun BottomBar(navController: NavHostController) {
  val screens =
      listOf(
          BottomBarScreen.Home,
          BottomBarScreen.Flight,
          BottomBarScreen.Chat,
          BottomBarScreen.Calendar,
      )
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentDestination = navBackStackEntry?.destination

  BottomNavigation {
    screens.forEach { screen ->
      AddItem(
          screen = screen, currentDestination = currentDestination, navController = navController)
    }
  }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
  BottomNavigationItem(
      label = { Text(text = screen.title) },
      icon = {
        Icon(painter = painterResource(id = screen.icon), contentDescription = "Navigation Icon")
      },
      selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
      unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.disabled),
      onClick = {
        navController.navigate(screen.route) {
          popUpTo(navController.graph.findStartDestination().id)
          launchSingleTop = true
        }
      })
}
