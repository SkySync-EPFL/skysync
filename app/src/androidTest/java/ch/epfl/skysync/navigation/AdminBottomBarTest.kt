package ch.epfl.skysync.navigation

import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.ContextConnectivityStatus
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.viewmodel.InFlightViewModel
import ch.epfl.skysync.viewmodel.MessageListenerViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AdminBottomBarTest {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: NavHostController
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()

  @Before
  fun setUp() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
    composeTestRule.setContent {
      val repository = Repository(db)
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      val inFlightViewModel = InFlightViewModel.createViewModel(repository)
      val messageListenerViewModel = MessageListenerViewModel.createViewModel()
      val context = LocalContext.current
      val connectivityStatus = remember { ContextConnectivityStatus(context) }
      NavHost(navController = navController, startDestination = Route.MAIN) {
        homeGraph(
            repository,
            navController,
            dbs.admin1.id,
            inFlightViewModel,
            messageListenerViewModel,
            connectivityStatus)
      }
    }
    composeTestRule.waitUntil {
      val nodes = composeTestRule.onAllNodesWithText("Upcoming flights")
      nodes.fetchSemanticsNodes().isNotEmpty()
    }
  }

  @Test
  fun verifyHomeIsStartDestination() {
    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.ADMIN_HOME)
  }

  @Test
  fun routeIsRightIfClickOnCalendar() {
    composeTestRule.onNodeWithText("Calendar").performClick()

    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.ADMIN_AVAILABILITY_CALENDAR)
  }

  @Test
  fun routeIsRightIfClickOnUser() {
    composeTestRule.onNodeWithText("User").performClick()

    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.USER)
  }

  @Test
  fun routeIsRightIfClickOnStat() {
    composeTestRule.onNodeWithText("Stats").performClick()
    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.STATS)
  }

  @Test
  fun routeIsRightIfClickOnChat() {
    composeTestRule.onNodeWithText("Chat").performClick()

    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.ADMIN_CHAT)
  }
}
