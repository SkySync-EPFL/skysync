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
import ch.epfl.skysync.database.DateUtility.formatTime
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FlightFinishedTest {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)
  @RelaxedMockK lateinit var navController: NavHostController
  private val dbs = DatabaseSetup()
  private val flight = dbs.flight5

  @Before
  fun setUpNavHost() {
    composeTestRule.setContent { FlightDetails(flight, PaddingValues(0.dp)) }
  }

  /** to verify that a given field and its corresponding value is displayed */
  private fun fieldAndValueAreDisplayed(field: String, value: String) {
    composeTestRule.onNodeWithText(field).assertIsDisplayed()
    composeTestRule.onNodeWithText(value).assertIsDisplayed()
  }

  @Test
  fun operationalTimesAreDisplayed() {
    composeTestRule
        .onNodeWithTag("FlightDetailLazyColumn")
        .performScrollToNode(hasText("Operational times"))
    composeTestRule.onNodeWithText("Operational times").assertIsDisplayed()
    fieldAndValueAreDisplayed(
        "Takeoff time",
        DateUtility.localTimeToString(DateUtility.dateToLocalTime(flight.takeOffTime)))
    fieldAndValueAreDisplayed(
        "Landing time",
        DateUtility.localTimeToString(DateUtility.dateToLocalTime(flight.landingTime)))
    fieldAndValueAreDisplayed("Flight duration", formatTime(flight.flightTime))
  }

  @Test
  fun locationAreDisplayed() {
    composeTestRule
        .onNodeWithTag("FlightDetailLazyColumn")
        .performScrollToNode(hasText("Landing location"))
    fieldAndValueAreDisplayed(
        "Takeoff location",
        "(" +
            flight.takeOffLocation.latlng().latitude.toString() +
            "," +
            flight.takeOffLocation.latlng().longitude.toString() +
            ")")
    fieldAndValueAreDisplayed(
        "Landing location",
        "(" +
            flight.takeOffLocation.latlng().latitude.toString() +
            "," +
            flight.takeOffLocation.latlng().longitude.toString() +
            ")")
  }

  @Test
  fun teamAndColorsAreDisplayed() {
    composeTestRule.onNodeWithTag("FlightDetailLazyColumn").performScrollToNode(hasText("Team"))
    composeTestRule.onNodeWithText("COLOR ${flight.color}").assertIsDisplayed()
  }
}
