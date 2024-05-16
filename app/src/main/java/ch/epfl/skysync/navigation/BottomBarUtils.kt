package ch.epfl.skysync.navigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import ch.epfl.skysync.R

sealed class BottomBarScreen(val route: String, val title: String, @DrawableRes val icon: Int) {
  data object Home :
      BottomBarScreen(route = Route.CREW_HOME, title = "Home", icon = R.drawable.baseline_home_24)

  data object Flight :
      BottomBarScreen(
          route = Route.LAUNCH_FLIGHT, title = "Flight", icon = R.drawable.baseline_flight_24)

  data object Chat :
      BottomBarScreen(route = Route.CREW_CHAT, title = "Chat", icon = R.drawable.baseline_chat_24)

  data object Calendar :
      BottomBarScreen(
          route = Route.CREW_CALENDAR,
          title = "Calendar",
          icon = R.drawable.baseline_calendar_month_24)

  data object User :
      BottomBarScreen(route = Route.USER, title = "User", icon = R.drawable.baseline_person_24)

  data object Stats :
      BottomBarScreen(
          route = Route.STATS, title = "Stats", icon = R.drawable.baseline_equalizer_24)

  data object AdminHome :
      BottomBarScreen(route = Route.ADMIN_HOME, title = "Home", icon = R.drawable.baseline_home_24)

  data object AdminChat :
      BottomBarScreen(route = Route.ADMIN_CHAT, title = "Chat", icon = R.drawable.baseline_chat_24)

  data object AdminCalendar :
      BottomBarScreen(
          route = Route.ADMIN_CALENDAR,
          title = "Calendar",
          icon = R.drawable.baseline_calendar_month_24)
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
        if (currentDestination?.route != screen.route) {
          navController.navigate(screen.route) {
            popUpTo(navController.graph.findStartDestination().id)
            launchSingleTop = true
          }
        }
      })
}
