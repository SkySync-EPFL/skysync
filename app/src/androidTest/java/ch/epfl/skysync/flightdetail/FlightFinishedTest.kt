package ch.epfl.skysync.flightdetail

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.FlightDetails
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.DateUtility
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FlightFinishedTest {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)
  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var navController: NavHostController
  private val dbs = DatabaseSetup()
  private val flight = dbs.flight5

  @Before
  fun setUpNavHost() {
    composeTestRule.setContent { FlightDetails(flight, PaddingValues(0.dp)) }
  }

  private fun helper(field: String, value: String) {
    composeTestRule.onNodeWithText(field).assertIsDisplayed()
    composeTestRule.onNodeWithText(value).assertIsDisplayed()
  }

  @Test
  fun operationalTimesAreDisplayed() {
    composeTestRule
        .onNodeWithTag("FlightDetailLazyColumn")
        .performScrollToNode(hasText("Operational times"))
    composeTestRule.onNodeWithText("Operational times").assertIsDisplayed()
    helper(
        "Takeoff time",
        DateUtility.localTimeToString(DateUtility.dateToLocalTime(flight.takeOffTime)))
    helper(
        "Landing time",
        DateUtility.localTimeToString(DateUtility.dateToLocalTime(flight.landingTime)))
    val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val date = Date(flight.flightTime)
    val formattedTime = formatter.format(date)
    val hours = formattedTime.substring(0, 2).toInt()
    val minutes = formattedTime.substring(3, 5).toInt()
    val seconds = formattedTime.substring(6, 8).toInt()
    helper("Flight Duration", "$hours:$minutes:$seconds")
  }

  @Test
  fun locationAreDisplayed() {
    composeTestRule
        .onNodeWithTag("FlightDetailLazyColumn")
        .performScrollToNode(hasText("Landing location"))
    helper("Takeoff location", flight.takeOffLocation.name)
    helper("Landing location", flight.landingLocation.name)
  }

  @Test
  fun teamAndColorsAreDisplayed() {
    composeTestRule.onNodeWithTag("FlightDetailLazyColumn").performScrollToNode(hasText("Team"))
    composeTestRule.onNodeWithText("COLOR ${flight.color}").assertIsDisplayed()
  }
}
