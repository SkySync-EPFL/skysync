package ch.epfl.skysync

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.navigation.homeGraph
import ch.epfl.skysync.screens.ShowFlightCalendar
import ch.epfl.skysync.screens.getStartOfWeek
import ch.epfl.skysync.viewmodel.UserViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class FlightCalendarTest {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController

  @Before
  fun setUpNavHost() {
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      ShowFlightCalendar(navController = navController, viewModel = UserViewModel.createViewModel(
        firebaseUser = null)
      )
    }
  }


  @Test
  fun routeIsRightIfClickOnNext() {
    var date = LocalDate.now()
    date = getStartOfWeek(date).plusWeeks(1)
    val stringCompare = date.format(DateTimeFormatter.ofPattern("EEE", Locale.getDefault()))
    composeTestRule.onNodeWithText("Next Week").performClick()
    composeTestRule.onNodeWithText(stringCompare).assertIsDisplayed()

  }

  @Test
  fun routeIsRightIfClickOnPrev() {
    var date = LocalDate.now()
    date = getStartOfWeek(date).minusWeeks(1)
    val stringCompare = date.format(DateTimeFormatter.ofPattern("EEE", Locale.getDefault()))
    composeTestRule.onNodeWithText("Prev Week").performClick()
    composeTestRule.onNodeWithText(stringCompare).assertIsDisplayed()

  }
}
