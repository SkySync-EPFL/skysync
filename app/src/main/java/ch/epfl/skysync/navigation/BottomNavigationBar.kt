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

    NavigationBar {
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
    navController: NavController
) {
    NavigationBarItem(
        label = { Text(text = screen.title) },
        icon = {
            Icon(painter = painterResource(id = screen.icon), contentDescription = "Navigation Icon")
        },
        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        })
}

sealed class BottomBarScreen(val route: String, val title: String, @DrawableRes val icon: Int) {
  object Home : BottomBarScreen(route = Route.HOME, title = "Home", icon = R.drawable.baseline_home_24)

  object Flight :
      BottomBarScreen(route = Route.FLIGHT, title = "Flight", icon = R.drawable.baseline_flight_24)

  object Chat : BottomBarScreen(route = Route.CHAT, title = "Chat", icon = R.drawable.baseline_chat_24)

  object Calendar :
      BottomBarScreen(
          route = Route.CALENDAR, title = "Calendar", icon = R.drawable.baseline_calendar_month_24
      )
}
