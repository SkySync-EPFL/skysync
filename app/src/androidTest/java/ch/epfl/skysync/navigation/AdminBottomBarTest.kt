package ch.epfl.skysync.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
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
    composeTestRule.setContent {
      AdminBottomBar(navController = navController)
      val repository = Repository(db)
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      // TODO put the graph of the admin view
      NavHost(navController = navController, startDestination = Route.MAIN) {
        homeGraph(repository, navController, dbs.admin1.id)
      }
    }
  }

  @Test
  fun verifyHomeIsStartDestination() {
    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.HOME)
  }

  @Test
  fun routeIsRightIfClickOnCalendar() {
    composeTestRule.onNodeWithText("Calendar").performClick()

    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.AVAILABILITY_CALENDAR)
  }

  @Test
  fun routeIsRightIfClickOnUser() {
    composeTestRule.onNodeWithText("User").performClick()

    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.FLIGHT)
  }

  @Test
  fun routeIsRightIfClickOnStat() {
    composeTestRule.onNodeWithText("User").performClick()
    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.FLIGHT)
  }

  @Test
  fun routeIsRightIfClickOnChat() {
    composeTestRule.onNodeWithText("Chat").performClick()

    val route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.CHAT)
  }
}
