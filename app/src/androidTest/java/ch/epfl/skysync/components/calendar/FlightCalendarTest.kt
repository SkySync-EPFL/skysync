package ch.epfl.skysync.screens.calendar

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.getStartOfWeek
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.navigation.homeGraph
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FlightCalendarTest {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController
  val dbs = DatabaseSetup()

  @Before
  fun setUpNavHost() = runTest {
    val db = FirestoreDatabase(useEmulator = true)
    val repository = Repository(db)
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      NavHost(navController = navController, startDestination = Route.MAIN) {
        homeGraph(repository, navController, dbs.admin1.id)
      }
    }
  }

  @Test
  fun swipeTest() {
    composeTestRule.onNodeWithText("Calendar").performClick()

    var route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.AVAILABILITY_CALENDAR)

    composeTestRule.onNodeWithTag(Route.FLIGHT_CALENDAR).performClick()
    route = navController.currentBackStackEntry?.destination?.route
    Assert.assertEquals(route, Route.FLIGHT_CALENDAR)

    var date = LocalDate.now()
    date = getStartOfWeek(date).minusWeeks(1)
    val nextWeek = date.toString() + TimeSlot.AM.toString()

    composeTestRule
        .onNodeWithTag("HorizontalPager")
        .performScrollToNode(hasTestTag(nextWeek))
        .assertIsDisplayed()

    date = LocalDate.now()
    date = getStartOfWeek(date).plusWeeks(1)
    val previousWeek = date.toString() + TimeSlot.AM.toString()

    composeTestRule
        .onNodeWithTag("HorizontalPager")
        .performScrollToNode(hasTestTag(previousWeek))
        .assertIsDisplayed()
  }
}