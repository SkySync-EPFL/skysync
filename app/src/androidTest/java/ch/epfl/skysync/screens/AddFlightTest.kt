package ch.epfl.skysync.screens

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.testing.TestNavHostController
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddFlightTest {

    @get:Rule val composeTestRule = createComposeRule()
    private lateinit var navController: TestNavHostController
    @Before
    fun setup(){
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            AddFlightScreen(navController = navController)
        }
    }
    @Test
    fun dateFieldIsDisplayed(){
        composeTestRule.onNodeWithText("Date").assertExists()
    }

    @Test
    fun timeSlotFieldIsDisplayed(){
        composeTestRule.onNodeWithText("Time Slot").assertExists()
    }

    @Test
    fun vehicleFieldIsDisplayed(){
        composeTestRule.onNodeWithText("Vehicle").assertExists()
    }
}


