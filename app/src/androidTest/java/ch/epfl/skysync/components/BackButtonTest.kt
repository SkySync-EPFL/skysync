package ch.epfl.skysync.components

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.testing.TestNavHostController
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BackButtonTest {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController

  @Before
  fun setUpNavHost() {
    composeTestRule.setContent { backbutton(BackClick = {}) }
  }

  @Test
  fun verifyBackButton() {
    composeTestRule.onNodeWithTag("BackButton").assertIsDisplayed()
  }

  @Test
  fun verifyBackButtonIsClickable() {
    composeTestRule.onNodeWithTag("BackButton").assertHasClickAction()
  }
}
