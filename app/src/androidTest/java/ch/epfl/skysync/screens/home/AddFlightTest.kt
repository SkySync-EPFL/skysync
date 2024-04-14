package ch.epfl.skysync.screens.home

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
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.navigation.homeGraph
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
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      NavHost(navController = navController, startDestination = Route.MAIN) {
        homeGraph(navController, null)
      }
      navController.navigate(Route.ADD_FLIGHT)
    }
  }

  @Test
  fun nbPassengerFieldIsDisplayed() {
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("nb Passenger"))
    composeTestRule.onNodeWithTag("nb Passenger").assertIsDisplayed()
  }

  @Test
  fun nbPassengerFieldWorksCorrectly() {
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("nb Passenger"))
    composeTestRule.onNodeWithTag("nb Passenger").assertTextContains("Number of passengers")
    composeTestRule.onNodeWithTag("nb Passenger").performClick()
    composeTestRule.onNodeWithTag("nb Passenger").performTextClearance()
    composeTestRule.onNodeWithTag("nb Passenger").performTextInput("1")
    composeTestRule.onNodeWithTag("nb Passenger").assertTextEquals("1")
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
        .performScrollToNode(hasTestTag("Vehicle Menu 1"))
    composeTestRule.onNodeWithTag("Vehicle Menu 1").assertIsDisplayed()
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
  fun addFlightButtonIsDisplayed() {
    composeTestRule.onNodeWithTag("Add Flight Button").assertIsDisplayed()
  }

  @Test
  fun checkAddAFlightWorksCorrectly() {
    composeTestRule
        .onNodeWithTag("Flight Lazy Column")
        .performScrollToNode(hasTestTag("nb Passenger"))
    composeTestRule.onNodeWithTag("nb Passenger").performClick()
    composeTestRule.onNodeWithTag("nb Passenger").performTextClearance()
    composeTestRule.onNodeWithTag("nb Passenger").performTextInput("1")

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
        .performScrollToNode(hasTestTag("Vehicle Menu 1"))
    composeTestRule.onNodeWithTag("Vehicle Menu 1").performClick()
    composeTestRule.onNodeWithTag("Vehicle 1").performClick()

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

    composeTestRule.onNodeWithTag("Add Flight Button").performClick()
    Assert.assertEquals(navController.currentDestination?.route, Route.HOME)
  }
}
