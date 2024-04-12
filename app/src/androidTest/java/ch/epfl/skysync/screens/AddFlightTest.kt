package ch.epfl.skysync.screens

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
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
    val context = ApplicationProvider.getApplicationContext<Context>()
    navController = TestNavHostController(context)
    navController.navigatorProvider.addNavigator(ComposeNavigator())
    composeTestRule.setContent {

      NavHost(navController = navController, startDestination = Route.MAIN) {
        homeGraph(navController, null)
      }
      navController.navigate(Route.ADD_FLIGHT)
    }
  }

  @Test
  fun nbPassengerFieldIsDisplayed() {
    composeTestRule.onNodeWithTag("nb Passenger").performScrollTo()
    composeTestRule.onNodeWithTag("nb Passenger").assertIsDisplayed()
  }

  @Test
  fun nbPassengerFieldWorksCorrectly() {
    composeTestRule.onNodeWithTag("nb Passenger").performScrollTo()
    composeTestRule.onNodeWithTag("nb Passenger").assertTextContains("Number of passengers")
    composeTestRule.onNodeWithTag("nb Passenger").performClick()
    composeTestRule.onNodeWithTag("nb Passenger").performTextClearance()
    composeTestRule.onNodeWithTag("nb Passenger").performTextInput("1")
    composeTestRule.onNodeWithTag("nb Passenger").assertTextEquals("1")
  }

  @Test
  fun dateFieldIsDisplayed() {
    composeTestRule.onNodeWithTag("Date Field").performScrollTo()
    composeTestRule.onNodeWithTag("Date Field").assertIsDisplayed()
  }

  @Test
  fun dateFieldIsClickable() {
    composeTestRule.onNodeWithTag("Date Field").performScrollTo()
    composeTestRule.onNodeWithTag("Date Field").performClick()
    composeTestRule.onNodeWithText("OK").performClick()
  }

  @Test
  fun flightTypeFieldIsDisplayed() {
    composeTestRule.onNodeWithTag("Flight Type Menu").performScrollTo()
    composeTestRule.onNodeWithTag("Flight Type Menu").assertIsDisplayed()
  }

  @Test
  fun vehicleFieldIsDisplayed() {
    composeTestRule.onNodeWithTag("Vehicle Menu").performScrollTo()
    composeTestRule.onNodeWithTag("Vehicle Menu").assertIsDisplayed()
  }

  @Test
  fun timeSlotFieldIsDisplayed() {
    composeTestRule.onNodeWithTag("Time Slot Menu").performScrollTo()
    composeTestRule.onNodeWithTag("Time Slot Menu").assertIsDisplayed()
  }

  @Test
  fun timeSlotFieldIsClickable() {
    composeTestRule.onNodeWithTag("Time Slot Menu").performScrollTo()
    composeTestRule.onNodeWithTag("Time Slot Menu").performClick()
    composeTestRule.onNodeWithText("PM", substring = true).performClick()
  }

  @Test
  fun balloonFieldIsDisplayed() {
    composeTestRule.onNodeWithTag("Balloon Menu").performScrollTo()
    composeTestRule.onNodeWithTag("Balloon Menu").assertIsDisplayed()
  }

  @Test
  fun basketFieldIsDisplayed() {
    composeTestRule.onNodeWithTag("Basket Menu").performScrollTo()
    composeTestRule.onNodeWithTag("Basket Menu").assertIsDisplayed()
  }

  @Test
  fun fondueFieldIsDisplayedCorrectly() {
    composeTestRule.onNodeWithTag("Fondue Role Field").assertIsNotDisplayed()

    composeTestRule.onNodeWithTag("Flight Type Menu").performScrollTo()
    composeTestRule.onNodeWithTag("Flight Type Menu").performClick()
    composeTestRule.onNodeWithText("Fondue").performClick()
    composeTestRule.onNodeWithTag("Fondue Role Field").performScrollTo()
    composeTestRule.onNodeWithTag("Fondue Role Field").assertIsDisplayed()
  }

  @Test
  fun addFlightButtonIsDisplayed() {
    composeTestRule.onNodeWithTag("Add Flight Button").assertIsDisplayed()
  }

  @Test
  fun checkAddAFlightWorksCorrectly() {
    composeTestRule.onNodeWithTag("nb Passenger").performScrollTo()
    composeTestRule.onNodeWithTag("nb Passenger").performClick()
    composeTestRule.onNodeWithTag("nb Passenger").performTextClearance()
    composeTestRule.onNodeWithTag("nb Passenger").performTextInput("1")

    composeTestRule.onNodeWithTag("Date Field").performScrollTo()
    composeTestRule.onNodeWithTag("Date Field").performClick()
    composeTestRule.onNodeWithText("OK").performClick()

    composeTestRule.onNodeWithTag("Flight Type Menu").performScrollTo()
    composeTestRule.onNodeWithTag("Flight Type Menu").performClick()
    composeTestRule.onNodeWithTag("Flight Type 1").performClick()

    composeTestRule.onNodeWithTag("Vehicle Menu").performScrollTo()
    composeTestRule.onNodeWithTag("Vehicle Menu").performClick()
    composeTestRule.onNodeWithTag("Vehicle 1").performClick()

    composeTestRule.onNodeWithTag("Balloon Menu").performScrollTo()
    composeTestRule.onNodeWithTag("Balloon Menu").performClick()
    composeTestRule.onNodeWithTag("Balloon 1").performClick()

    composeTestRule.onNodeWithTag("Time Slot Menu").performScrollTo()
    composeTestRule.onNodeWithTag("Time Slot Menu").performClick()
    composeTestRule.onNodeWithTag("Time Slot 1").performClick()

    composeTestRule.onNodeWithTag("Basket Menu").performScrollTo()
    composeTestRule.onNodeWithTag("Basket Menu").performClick()
    composeTestRule.onNodeWithTag("Basket 1").performClick()

    composeTestRule.onNodeWithTag("Add Flight Button").performClick()
    Assert.assertEquals(navController.currentDestination?.route, Route.HOME)
  }
}
