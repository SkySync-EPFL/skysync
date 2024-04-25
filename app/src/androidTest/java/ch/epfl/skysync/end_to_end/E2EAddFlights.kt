package ch.epfl.skysync.end_to_end

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.navigation.homeGraph
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class E2EAddFlights {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController
  private val db = FirestoreDatabase()
  private val repository = Repository(db)

  @Before
  fun setUpNavHost() {
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      NavHost(navController = navController, startDestination = Route.MAIN) {
        homeGraph(repository, navController, null)
      }
    }
  }

  @Test
  fun addFlightAsAdmin() {

    composeTestRule.onNodeWithTag("addFlightButton").performClick()
    var route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(Route.ADD_FLIGHT, route)
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("nb Passenger"))
    composeTestRule.onNodeWithTag("nb Passenger").performClick()
    composeTestRule.onNodeWithTag("nb Passenger").performTextClearance()
    composeTestRule.onNodeWithTag("nb Passenger").performTextInput("13")

    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Date Field"))
    composeTestRule.onNodeWithTag("Date Field").performClick()
    composeTestRule.onNodeWithText("OK").performClick()

    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Flight Type Menu"))
    composeTestRule.onNodeWithTag("Flight Type Menu").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("Flight Type 1").performClick()

    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Vehicle 0 Menu"))
    composeTestRule.onNodeWithTag("Vehicle 0 Menu").performClick()
    composeTestRule.onNodeWithTag("Vehicle 0 1").performClick()

    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Time Slot Menu"))
    composeTestRule.onNodeWithTag("Time Slot Menu").performClick()
    composeTestRule.onNodeWithTag("Time Slot 1").performClick()

    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Balloon Menu"))
    composeTestRule.onNodeWithTag("Balloon Menu").performClick()
    composeTestRule.onNodeWithTag("Balloon 1").performClick()

    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Basket Menu"))
    composeTestRule.onNodeWithTag("Basket Menu").performClick()
    composeTestRule.onNodeWithTag("Basket 1").performClick()
    val title1 = "Add Flight"
    composeTestRule.onNodeWithTag("$title1 Button").performClick()
    route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(Route.HOME, route)
    var flightIsCreated = false
    runTest {
      val flights = repository.flightTable.getAll(onError = { assertNull(it) })
      flightIsCreated = flights.any { it.nPassengers == 13 }
    }
    Assert.assertEquals(true, flightIsCreated)
  }
}
