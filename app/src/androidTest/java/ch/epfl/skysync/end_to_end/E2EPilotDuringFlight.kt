package ch.epfl.skysync.end_to_end

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import androidx.test.InstrumentationRegistry.getTargetContext
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.navigation.homeGraph
import ch.epfl.skysync.viewmodel.ChatViewModel
import ch.epfl.skysync.viewmodel.MessageListenerSharedViewModel
import ch.epfl.skysync.viewmodel.TimerViewModel
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class E2EPilotDuringFlight {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule
  var permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)
  lateinit var navController: TestNavHostController
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  private val repository = Repository(db)
  private lateinit var messageListenerSharedViewModel: MessageListenerSharedViewModel
  private lateinit var chatViewModel: ChatViewModel

  @Before
  fun setUpNavHost() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
    composeTestRule.setContent {
      messageListenerSharedViewModel = MessageListenerSharedViewModel.createViewModel()
      chatViewModel =
          ChatViewModel.createViewModel(dbs.pilot1.id, messageListenerSharedViewModel, repository)
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      val t = TimerViewModel.createViewModel()
      NavHost(navController = navController, startDestination = Route.MAIN) {
        homeGraph(repository, navController, dbs.pilot1.id, t)
      }
    }
    composeTestRule.waitUntil {
      val nodes = composeTestRule.onAllNodesWithText("Upcoming flights")
      nodes.fetchSemanticsNodes().isNotEmpty()
    }
  }

  @Test
  fun addFlightAsAdmin() {
    runTest {
      // Refreshes chat and user data asynchronously
      chatViewModel.refresh().join()
      chatViewModel.refreshUser().join()

      // Clicks on the "Flight" button to navigate to the flight screen
      composeTestRule.onNodeWithText("Flight").performClick()
      var route = navController.currentBackStackEntry?.destination?.route
      Assert.assertEquals(Route.FLIGHT, route)

      // Asserts the presence of the timer
      composeTestRule.onNodeWithTag("Timer").assertExists()

      // Starts the timer by clicking on the "Start Button"
      composeTestRule.onNodeWithTag("Start Button").performClick()

      // Asserts the presence of the map and "Locate Me" button
      composeTestRule.onNodeWithTag("Map").assertExists()
      composeTestRule.onNodeWithContentDescription("Locate Me").assertIsDisplayed()

      // Opens flight information and asserts the display of navigation information
      composeTestRule.onNodeWithContentDescription("Flight infos").performClick()
      composeTestRule
          .onNodeWithText("X Speed: 0.0 m/s\nY Speed: 0.0 m/s\nAltitude: 0.0 m\nBearing: 0.0 °")
          .assertIsDisplayed()

      // Navigates to the chat screen
      composeTestRule.onNodeWithText("Chat").performClick()
      route = navController.currentBackStackEntry?.destination?.route
      Assert.assertEquals(Route.CHAT, route)

      // Refreshes chat data asynchronously
      val index = 0
      chatViewModel.refresh().join()
      chatViewModel.refreshUser().join()

      // Waits for the UI to become idle
      composeTestRule.waitForIdle()

      // clicks on group chat
      composeTestRule.waitUntil(5000) {
        composeTestRule.onAllNodesWithTag("GroupCard$index").fetchSemanticsNodes().isNotEmpty()
      }
      composeTestRule.onNodeWithTag("GroupCard$index").performClick()

      // Inputs and sends a message
      composeTestRule.onNodeWithTag("ChatInput").performTextInput("Hello")
      composeTestRule.onNodeWithTag("SendButton").performClick()

      // Returns to the flight screen with the timer still running
      composeTestRule.onNodeWithText("Flight").performClick()
      composeTestRule.waitForIdle()
      route = navController.currentBackStackEntry?.destination?.route
      Assert.assertEquals(Route.FLIGHT, route)

      // Stops the timer by clicking on the "Stop Button"
      composeTestRule.onNodeWithTag("Stop Button").performClick()
    }
  }

  @After
  fun tearDown() {
    val it = android.Manifest.permission.ACCESS_FINE_LOCATION
    InstrumentationRegistry.getInstrumentation()
        .uiAutomation
        .executeShellCommand("pm revoke ${getTargetContext().packageName} $it")
  }
}
