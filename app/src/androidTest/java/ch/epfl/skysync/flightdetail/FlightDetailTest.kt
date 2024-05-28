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
import ch.epfl.skysync.models.flight.FlightStatus
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FlightDetailTest {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)
  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var navController: NavHostController
  private val dbs = DatabaseSetup()
  private val flight = dbs.flight4

  @Before
  fun setUpNavHost() {
    composeTestRule.setContent { FlightDetails(flight, PaddingValues(0.dp)) }
  }

  /** Helper function to verify that a given field and its corresponding value is displayed */
  private fun verifyDisplay(field: String, value: String) {
    composeTestRule.onNodeWithText(field).assertIsDisplayed()
    composeTestRule.onNodeWithText(value).assertIsDisplayed()
  }

  @Test
  fun statusIsDisplayed() {
    verifyDisplay("Flight status", FlightStatus.CONFIRMED.text)
  }

  @Test
  fun dateValueIsDisplayed() {
    verifyDisplay("Day of flight", DateUtility.localDateToString(flight.date))
  }

  @Test
  fun timeSlotIsDisplayed() {
    verifyDisplay("Time slot", DateUtility.localDateToString(flight.date))
  }

  @Test
  fun numberOfPaxValueIsDisplayed() {
    verifyDisplay("Number of Passengers", flight.nPassengers.toString())
  }

  @Test
  fun flightTypeValueIsDisplayed() {
    verifyDisplay("Flight type", flight.flightType.name)
  }

  @Test
  fun balloonAndValueIsDisplayed() {
    verifyDisplay("Balloon", flight.balloon.name)
  }

  @Test
  fun basketAndValueIsDisplayed() {
    verifyDisplay("Basket", flight.basket.name)
  }

  @Test
  fun vehiclesAndValuesAreDisplayed() {
    composeTestRule.onNodeWithText("Vehicles").assertIsDisplayed()
    for (v in flight.vehicles) {
      composeTestRule.onNodeWithText(v.name).assertIsDisplayed()
    }
  }

  @Test
  fun teamAndValuesAreDisplayed() {
    composeTestRule.onNodeWithTag("FlightDetailLazyColumn").performScrollToNode(hasText("Team"))
    composeTestRule.onNodeWithText("COLOR ${flight.color}").assertIsDisplayed()
    for (role in flight.team.roles) {
      val metric = role.roleType.description
      val firstname = role.assignedUser?.firstname
      val lastname = role.assignedUser?.lastname
      composeTestRule.onNodeWithTag("Metric$metric$firstname").assertIsDisplayed()
      composeTestRule.onNodeWithTag("Metric$metric$lastname").assertIsDisplayed()
    }
  }

  @Test
  fun meetUpTimesAndValuesAreDisplayed() {
    composeTestRule
        .onNodeWithTag("FlightDetailLazyColumn")
        .performScrollToNode(hasText("Meet up times"))
    composeTestRule.onNodeWithText("Team meet up time").assertIsDisplayed()
    composeTestRule
        .onNodeWithText(DateUtility.localTimeToString(flight.meetupTimeTeam))
        .assertIsDisplayed()
    composeTestRule.onNodeWithText("Team departure time").assertIsDisplayed()
    composeTestRule
        .onNodeWithText(DateUtility.localTimeToString(flight.departureTimeTeam))
        .assertIsDisplayed()
    composeTestRule.onNodeWithText("Passengers meet up time").assertIsDisplayed()
    composeTestRule
        .onNodeWithText(DateUtility.localTimeToString(flight.meetupTimePassenger))
        .assertIsDisplayed()
  }

  @Test
  fun passengersMeetUpLocationAreDisplayed() {
    composeTestRule
        .onNodeWithTag("FlightDetailLazyColumn")
        .performScrollToNode(hasText("Passengers meet up location"))
    composeTestRule.onNodeWithText(flight.meetupLocationPassenger).assertIsDisplayed()
  }

  @Test
  fun remarksAreDisplayed() {
    composeTestRule.onNodeWithTag("FlightDetailLazyColumn").performScrollToNode(hasText("Remarks"))
    flight.remarks.forEach { r -> composeTestRule.onNodeWithText(r).assertIsDisplayed() }
  }
}
