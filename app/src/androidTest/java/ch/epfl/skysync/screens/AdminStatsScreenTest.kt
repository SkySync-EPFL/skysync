package ch.epfl.skysync.screens

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.screens.admin.AdminStatsScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AdminStatsScreenTest {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController

  @Before
  fun setUp() {
    composeTestRule.setContent {
      navController = TestNavHostController(LocalContext.current)
      AdminStatsScreen(navController)
    }
  }

  @Test
  fun isTextDisplayed() {
    composeTestRule.onNodeWithText("Feature not available").assertIsDisplayed()
  }
}
