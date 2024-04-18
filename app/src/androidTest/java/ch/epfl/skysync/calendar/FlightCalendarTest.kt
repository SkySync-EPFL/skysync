package ch.epfl.skysync.calendar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.components.FlightCalendar
import ch.epfl.skysync.components.getStartOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FlightCalendarTest {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController

  @Before
  fun setUpNavHost() {
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      FlightCalendar(
          padding = PaddingValues(0.dp),
          onAvailabilityCalendarClick = {},
          getFirstFlightByDate = { _, _ -> null },
          onFlightClick = {})
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
