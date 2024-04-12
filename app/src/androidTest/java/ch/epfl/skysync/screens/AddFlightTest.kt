package ch.epfl.skysync.screens

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.navigation.Route
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddFlightTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var navController: TestNavHostController

  @Before
  fun setup() {
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      AddFlightScreen(navController = navController, mutableListOf<PlannedFlight>())
    }
  }

  @Test
  fun nbPassengerFieldIsDisplayed() {
    composeTestRule.onNodeWithTag("nb Passenger").assertExists()
  }

  @Test
  fun nbPassengerFieldWorksCorrectly() {
    composeTestRule.onNodeWithTag("nb Passenger").assertTextContains("Number of passengers")
    composeTestRule.onNodeWithTag("nb Passenger").performClick()
    composeTestRule.onNodeWithTag("nb Passenger").performTextClearance()
    composeTestRule.onNodeWithTag("nb Passenger").performTextInput("1")
    composeTestRule.onNodeWithTag("nb Passenger").assertTextEquals("1")
  }

  @Test
  fun dateFieldIsDisplayed() {
    composeTestRule.onNodeWithTag("Date Field").assertExists()
  }

  @Test
  fun dateFieldIsClickable() {
    composeTestRule.onNodeWithTag("Date Field").performClick()
    composeTestRule.onNodeWithText("OK").performClick()
  }

  @Test
  fun flightTypeFieldIsDisplayed() {
    composeTestRule.onNodeWithTag("Flight Type Menu").assertExists()
  }

  @Test
  fun timeSlotFieldIsDisplayed() {
    composeTestRule.onNodeWithTag("Time Slot Menu").assertExists()
  }

  @Test
  fun balloonFieldIsDisplayed() {
    composeTestRule.onNodeWithTag("Balloon Menu").assertExists()
  }

  @Test
  fun basketFieldIsDisplayed() {
    composeTestRule.onNodeWithTag("Basket Menu").assertExists()
  }

  @Test
  fun fondueFieldIsDisplayedCorrectly() {
    composeTestRule.onNodeWithTag("Fondue Role Field").assertDoesNotExist()

    composeTestRule.onNodeWithTag("Flight Type Menu").performClick()
    composeTestRule.onNodeWithText("Fondue").performClick()
    composeTestRule.onNodeWithTag("Fondue Role Field").assertExists()
  }

  @Test
  fun addFlightButtonIsDisplayed() {
    composeTestRule.onNodeWithTag("Add Flight Button").assertExists()
  }

  @Test
  fun checkAddAFlightWorksCorrectly() {
    composeTestRule.onNodeWithTag("nb Passenger").performClick()
    composeTestRule.onNodeWithTag("nb Passenger").performTextClearance()
    composeTestRule.onNodeWithTag("nb Passenger").performTextInput("1")
    composeTestRule.onNodeWithTag("Date Field").performClick()
    composeTestRule.onNodeWithText("OK").performClick()
    composeTestRule.onNodeWithTag("Flight Type Menu").performClick()
    composeTestRule.onNodeWithText("Fondue").performClick()
    composeTestRule.onNodeWithTag("Vehicle Menu").performClick()
    composeTestRule.onNodeWithText("Balloon").performClick()
    composeTestRule.onNodeWithTag("Time Slot Menu").performClick()
    composeTestRule.onNodeWithTag("Balloon Menu").performClick()
    composeTestRule.onNodeWithText("Balloon 1").performClick()
    composeTestRule.onNodeWithTag("Basket Menu").performClick()
    composeTestRule.onNodeWithText("Basket 1").performClick()
    composeTestRule.onNodeWithTag("Add Flight Button").performClick()
    Assert.assertEquals(navController.currentDestination?.route, Route.HOME)
  }
}
