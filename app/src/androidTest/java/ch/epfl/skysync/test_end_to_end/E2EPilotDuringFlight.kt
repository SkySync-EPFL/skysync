package ch.epfl.skysync.test_end_to_end

import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
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
import androidx.test.rule.GrantPermissionRule
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.ContextConnectivityStatus
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.navigation.homeGraph
import ch.epfl.skysync.viewmodel.ChatViewModel
import ch.epfl.skysync.viewmodel.InFlightViewModel
import ch.epfl.skysync.viewmodel.MessageListenerViewModel
import kotlinx.coroutines.test.runTest
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
  private lateinit var messageListenerViewModel: MessageListenerViewModel
  private lateinit var chatViewModel: ChatViewModel
  private lateinit var inFlightViewModel: InFlightViewModel

  @Before
  fun setUpNavHost() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
    composeTestRule.setContent {
      messageListenerViewModel = MessageListenerViewModel.createViewModel()
      chatViewModel =
          ChatViewModel.createViewModel(dbs.pilot1.id, messageListenerViewModel, repository)
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      inFlightViewModel = InFlightViewModel.createViewModel(repository)
      val messageListenerViewModel = MessageListenerViewModel.createViewModel()
      val context = LocalContext.current
      val connectivityStatus = remember { ContextConnectivityStatus(context) }
      NavHost(navController = navController, startDestination = Route.MAIN) {
        homeGraph(
            repository,
            navController,
            dbs.pilot1.id,
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
  fun useMapAndChatAsPilot() = runTest {
    // Refreshes chat and user data asynchronously
    chatViewModel.refresh().join()
    chatViewModel.refreshUser().join()
    inFlightViewModel.init(dbs.pilot1.id).join()

    // Clicks on the "Flight" button to navigate to the flight screen
    composeTestRule.onNodeWithText("Flight").performClick()
    var route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(Route.LAUNCH_FLIGHT, route)
    var usedFlightId = ""
    composeTestRule.waitUntil(3000) {
      composeTestRule.onNodeWithTag("flightCard${dbs.flight1.id}").isDisplayed() ||
        composeTestRule.onNodeWithTag("flightCard${dbs.flight2.id}").isDisplayed() ||
        composeTestRule.onNodeWithTag("flightCard${dbs.flight3.id}").isDisplayed() ||
        composeTestRule.onNodeWithTag("flightCard${dbs.flight4.id}").isDisplayed()

    }
    for (f in listOf(dbs.flight1, dbs.flight2, dbs.flight3, dbs.flight4)) {
      if (composeTestRule.onNodeWithTag("flightCard${f.id}").isDisplayed()) {
        usedFlightId = f.id
        break
      }
    }
    composeTestRule.onNodeWithTag("flightCard${usedFlightId}").performClick()

    // Asserts the presence of the timer
    composeTestRule.waitUntil(3000) { composeTestRule.onNodeWithTag("Timer").isDisplayed() }

    // Starts the timer by clicking on the "Start Button"
    composeTestRule.onNodeWithTag("Start Button").performClick()

    // Asserts the presence of the map and "Locate Me" button
    composeTestRule.waitUntil(3000){
            composeTestRule.onNodeWithContentDescription("Locate Me").isDisplayed()
    }

    // Opens flight information and asserts the display of navigation information
    composeTestRule.onNodeWithContentDescription("Flight infos").performClick()
    composeTestRule
        .onNodeWithText(
            "Horizontal Speed: 0.00 m/s\nVertical Speed: 0.00 m/s\nAltitude: 0 m\nBearing: 0.00 °")
        .assertIsDisplayed()

    // Navigates to the chat screen
    composeTestRule.onNodeWithText("Chat").performClick()
    route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(Route.CREW_CHAT, route)

    // Refreshes chat data asynchronously
    val index = 0
    chatViewModel.refresh().join()
    chatViewModel.refreshUser().join()

    // Waits for the UI to become idle
    composeTestRule.waitForIdle()

    // clicks on group chat
    composeTestRule.waitUntil(3000) {
      composeTestRule.onAllNodesWithTag("GroupCard$index").fetchSemanticsNodes().isNotEmpty()
    }
    composeTestRule.onNodeWithTag("GroupCard$index").performClick()

    // Inputs and sends a message
    composeTestRule.onNodeWithTag("ChatInput").performTextInput("Hello")
    composeTestRule.onNodeWithTag("SendButton").performClick()

    // Returns to the flight screen with the timer still running
    composeTestRule.onNodeWithText("Flight").performClick()
    composeTestRule.waitUntil(3000) {
      composeTestRule.onNodeWithTag("Stop Button").isDisplayed()
    }
    route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(Route.FLIGHT, route)

    // Stops the timer by clicking on the "Stop Button"
    composeTestRule.onNodeWithTag("Stop Button").performClick()
    composeTestRule.waitUntil(3000) { composeTestRule.onNodeWithTag("Clear Button").isDisplayed() }
    composeTestRule.onNodeWithTag("Clear Button").performClick()
    composeTestRule.waitUntil(3000) {
        composeTestRule.onNodeWithText("Upcoming flights").isDisplayed()
    }
    route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(Route.CREW_HOME, route)
  }
}
