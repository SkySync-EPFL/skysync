package ch.epfl.skysync.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.testing.TestNavHostController
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HeaderTest {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController

  @Before
  fun setUpNavHost() {
    composeTestRule.setContent { Header(backClick = {}, title = "Test Title") }
  }

  @Test
  fun verifyHeader() {
    composeTestRule.onNodeWithTag("HeaderTitle").assertIsDisplayed()
  }
}
