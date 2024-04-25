package ch.epfl.skysync.navigation

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.AvailabilityCalendarNew
import ch.epfl.skysync.screens.CalendarScreen
import ch.epfl.skysync.viewmodel.CalendarViewModel

fun NavGraphBuilder.personalCalendar(
    repository: Repository,
    navController: NavHostController,
    uid: String?
) {
  navigation(startDestination = Route.AVAILABILITY_CALENDAR, route = Route.CALENDAR) {
    composable(Route.AVAILABILITY_CALENDAR) {
      val viewModel =
          CalendarViewModel.createViewModel(
              uid!!, repository.userTable, repository.availabilityTable)
      CalendarScreen(navController, Route.AVAILABILITY_CALENDAR, viewModel)
    }
    composable(Route.PERSONAL_FLIGHT_CALENDAR) {
      val viewModel =
          CalendarViewModel.createViewModel(
              uid!!, repository.userTable, repository.availabilityTable)
      CalendarScreen(navController, Route.PERSONAL_FLIGHT_CALENDAR, viewModel)
    }
  }
}

fun NavGraphBuilder.personalCalendarNew(
    repository: Repository,
    navController: NavHostController,
    uid: String?
) {
  val tabs = mapOf(Route.FLIGHT_CALENDAR to 0, Route.AVAILABILITY_CALENDAR to 1)
  navigation(startDestination = Route.AVAILABILITY_CALENDAR, route = Route.CALENDAR) {
    composable(Route.PERSONAL_FLIGHT_CALENDAR) {
      val viewModel =
          CalendarViewModel.createViewModel(
              uid!!, repository.userTable, repository.availabilityTable)
      // CalendarScreenNew(navController, Route.PERSONAL_FLIGHT_CALENDAR, viewModel)
    }

    composable(Route.AVAILABILITY_CALENDAR) {
      val viewModel =
          CalendarViewModel.createViewModel(
              uid!!, repository.userTable, repository.availabilityTable)
      AvailabilityCalendarNew(
          topBar = {
            TopBar(tab = Route.AVAILABILITY_CALENDAR, tabs = tabs) { onClickT(it, navController) }
          },
          navController = navController,
          viewModel = viewModel) {}
    }
  }
}

fun onClickT(tabIndex: Int, navController: NavHostController) {
  when (tabIndex) {
    0 -> navController.navigate(Route.PERSONAL_FLIGHT_CALENDAR)
    1 -> navController.navigate(Route.AVAILABILITY_CALENDAR)
  }
}

@Composable
fun TopBar(tab: String, tabs: Map<String, Int>, onclick: (Int) -> Unit) {
  val tabIndex = tabs[tab]
  if (tabIndex != null) {
    TabRow(selectedTabIndex = tabIndex) {
      tabs.forEach { (key, value) ->
        Tab(text = { Text(key) }, selected = tabIndex == value, onClick = { onclick(value) })
      }
    }
  }
}
