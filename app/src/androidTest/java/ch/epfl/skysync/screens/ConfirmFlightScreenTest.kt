package ch.epfl.skysync.screens

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.navigation.homeGraph
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ConfirmFlightScreenTest {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  val repository = Repository(db)

  @Before
  fun setUpNavHost() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      NavHost(navController = navController, startDestination = Route.MAIN) {
        homeGraph(repository, navController, dbs.admin1.id)
      }
    }
    composeTestRule.waitUntil {
      val nodes = composeTestRule.onAllNodesWithText("Upcoming flights")
      nodes.fetchSemanticsNodes().isNotEmpty()
    }
  }

  @Test
  fun backStackIsRightIfClickOnFlightDetailsThenFlightConfirm() = runTest {
    val assignedFlight =
        listOf(dbs.flight1, dbs.flight2, dbs.flight3, dbs.flight4).sortedBy { flight: Flight ->
          flight.id
        }
    val retrievedFlights =
        repository.flightTable.getAll(onError = { Assert.assertNull(it) }).sortedBy { flight: Flight
          ->
          flight.id
        }
    Assert.assertEquals(assignedFlight, retrievedFlights)
    composeTestRule.onNodeWithText("Home").performClick()

    val canBeConfirmedFlights =
        assignedFlight.filterIsInstance<PlannedFlight>().filter { it.readyToBeConfirmed() }

    for (flight in canBeConfirmedFlights) {
      composeTestRule
          .onNodeWithTag("HomeLazyList")
          .performScrollToNode(hasTestTag("flightCard${flight.id}"))
      composeTestRule.onNodeWithTag("flightCard${flight.id}").performClick()
      var route = navController.currentBackStackEntry?.destination?.route
      Assert.assertEquals(Route.FLIGHT_DETAILS + "/{Flight ID}", route)

      composeTestRule.onNodeWithText("Confirm").performClick()

      composeTestRule.waitUntil(2500) {
        composeTestRule.onAllNodesWithText("Balloon").fetchSemanticsNodes().isNotEmpty()
      }

      composeTestRule.onNodeWithTag("LazyList").performScrollToNode(hasText("Enter Remark"))
      composeTestRule.onNodeWithText("Confirm").assertDoesNotExist()

      val setTime = composeTestRule.onAllNodesWithText("Set Time")
      for (i in 0 until setTime.fetchSemanticsNodes().size) {
        setTime[i].performClick()
      }
      composeTestRule.onNodeWithTag("LazyList").performScrollToNode(hasText("Confirm"))
      composeTestRule.onNodeWithText("Confirm").performClick()
      composeTestRule.onNodeWithTag("AlertDialogConfirm").performClick()
      route = navController.currentBackStackEntry?.destination?.route
      Assert.assertEquals(Route.HOME, route)
    }
  }
}
