package ch.epfl.skysync.components

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.components.forms.FlightForm
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Vehicle
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FlightFormTest {

  @get:Rule val composeTestRule = createComposeRule()
  private var navController: TestNavHostController = mockk("navController", relaxed = true)
  private val title: String = "Modify Flight"

  @Before
  fun setup() {
    composeTestRule.setContent {
      val allFlights = FlightType.ALL_FLIGHTS
      val allVehicles =
          listOf(
              Vehicle("Vehicle 1"),
              Vehicle("Vehicle 2"),
          )
      val allBalloons =
          listOf(
              Balloon("Balloon 1", BalloonQualification.SMALL),
              Balloon("Balloon 2", BalloonQualification.MEDIUM),
          )
      val allBaskets =
          listOf(
              Basket("Basket 1", true),
              Basket("Basket 2", false),
          )
      val allRoleTypes = RoleType.entries
      navController = TestNavHostController(LocalContext.current)
      FlightForm(
          currentFlight = null,
          navController = navController,
          modifyMode = false,
          title = title,
          allFlightTypes = allFlights,
          allRoleTypes = allRoleTypes,
          allVehicles = allVehicles,
          allBalloons = allBalloons,
          allBaskets = allBaskets,
          flightAction = { _ -> })
    }
  }

  @Test
  fun nbPassengerFieldIsDisplayed() {
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Number of passengers"))
    composeTestRule.onNodeWithTag("Number of passengers").assertIsDisplayed()
  }

  @Test
  fun nbPassengerFieldWorksCorrectly() {
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Number of passengers"))
    composeTestRule.onNodeWithTag("Number of passengers").performClick()
    composeTestRule.onNodeWithTag("Number of passengers").performTextClearance()
    composeTestRule.onNodeWithTag("Number of passengers").performTextInput("1")
    composeTestRule.onNodeWithTag("Number of passengers").assertTextEquals("1")
  }

  @Test
  fun dateFieldIsDisplayed() {
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Date Field"))
    composeTestRule.onNodeWithTag("Date Field").assertIsDisplayed()
  }

  @Test
  fun dateFieldIsClickable() {
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Date Field"))
    composeTestRule.onNodeWithTag("Date Field").performClick()
    composeTestRule.onNodeWithText("OK").performClick()
  }

  @Test
  fun flightTypeFieldIsDisplayed() {
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Flight Type Menu"))
    composeTestRule.onNodeWithTag("Flight Type Menu").assertIsDisplayed()
  }

  @Test
  fun vehicleFieldIsDisplayed() {
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Vehicle 0 Menu"))
    composeTestRule.onNodeWithTag("Vehicle 0 Menu").assertIsDisplayed()
  }

  @Test
  fun timeSlotFieldIsDisplayed() {
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Time Slot Menu"))
    composeTestRule.onNodeWithTag("Time Slot Menu").assertIsDisplayed()
  }

  @Test
  fun timeSlotFieldIsClickable() {
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Time Slot Menu"))
    composeTestRule.onNodeWithTag("Time Slot Menu").performClick()
    composeTestRule.onNodeWithTag("Time Slot 1").performClick()
  }

  @Test
  fun balloonFieldIsDisplayed() {
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Balloon Menu"))
    composeTestRule.onNodeWithTag("Balloon Menu").assertIsDisplayed()
  }

  @Test
  fun basketFieldIsDisplayed() {
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Basket Menu"))
    composeTestRule.onNodeWithTag("Basket Menu").assertIsDisplayed()
  }

  @Test
  fun fondueFieldIsDisplayedCorrectly() {
    composeTestRule.onNodeWithText(RoleType.MAITRE_FONDUE.name).assertIsNotDisplayed()

    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Flight Type Menu"))
    composeTestRule.onNodeWithTag("Flight Type Menu").performClick()
    composeTestRule.onNodeWithText("Fondue").performClick()
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasText(RoleType.MAITRE_FONDUE.name))
    composeTestRule.onNodeWithText(RoleType.MAITRE_FONDUE.name).assertIsDisplayed()
  }

  @Test
  fun mainButtonIsDisplayed() {
    composeTestRule.onNodeWithTag("$title Button").assertIsDisplayed()
  }

  @Test
  fun checkAddAFlightWorksCorrectly() {
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Number of passengers"))
    composeTestRule.onNodeWithTag("Number of passengers").performClick()
    composeTestRule.onNodeWithTag("Number of passengers").performTextClearance()
    composeTestRule.onNodeWithTag("Number of passengers").performTextInput("1")

    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Date Field"))
    composeTestRule.onNodeWithTag("Date Field").performClick()
    composeTestRule.onNodeWithText("OK").performClick()

    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Flight Type Menu"))
    composeTestRule.onNodeWithTag("Flight Type Menu").performClick()
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

    composeTestRule.onNodeWithTag("$title Button").performClick()
  }

  @Test
  fun addATeamRoleWorksCorrectly() {
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Add Crew Button"))
    composeTestRule.onNodeWithTag("Add Crew Button").performClick()
    composeTestRule.onNodeWithTag("Role Type Menu").performClick()
    composeTestRule.onNodeWithText(RoleType.SERVICE_ON_BOARD.name).performClick()
    composeTestRule.onNodeWithTag("User Dialog Field").performClick()
    composeTestRule.onNodeWithTag("User Dialog Field").performTextInput("test")
    composeTestRule.onNodeWithTag("User Dialog Field").assertTextContains("test")
    composeTestRule.onNode(hasText("Add")).performClick()
    composeTestRule.onNodeWithTag("Flight Lazy Column").performScrollToNode(hasTestTag(" User 2"))
    composeTestRule.onNodeWithText(RoleType.SERVICE_ON_BOARD.name).assertIsDisplayed()
  }

  @Test
  fun addAVehicleWorksCorrectly() {
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Vehicle 0 Menu"))
    composeTestRule.onNodeWithTag("Vehicle 0 Menu").performClick()
    composeTestRule.onNodeWithTag("Vehicle 0 0").performClick()
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Add Vehicle Button"))
    composeTestRule.onNodeWithTag("Add Vehicle Button").performClick()
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Vehicle 1 Menu"))
    composeTestRule.onNodeWithTag("Vehicle 1 Menu").performClick()
    composeTestRule.onNodeWithTag("Vehicle 1 1").performClick()
  }

  @Test
  fun isErrorDisplayedCorrectly() {
    composeTestRule.onNodeWithTag("$title Button").performClick()
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Number of passengers"))
    composeTestRule.onNodeWithText("Please enter a valid number", true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("Number of passengers").performTextInput("1")
    composeTestRule.onNodeWithTag("$title Button").performClick()
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("Flight Type Menu"))
    composeTestRule.onNodeWithText("Please choose a flight type", true).assertIsDisplayed()
  }
}
