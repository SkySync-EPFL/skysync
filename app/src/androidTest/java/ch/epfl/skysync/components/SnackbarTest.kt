package ch.epfl.skysync.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SnackbarManagerTest {

  @Test
  fun testMessagesFlow_emitsMessages() = runBlockingTest {
    SnackbarManager.showMessage("Another test message")
    val flowResult = SnackbarManager.messagesFlow.first()
    assertEquals("Another test message", flowResult)
  }
}

@ExperimentalCoroutinesApi
class GlobalSnackbarHostTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun snackbarHost_displaysMessage() {
    // Use a test tag or text matcher to find the snackbar in the UI
    SnackbarManager.showMessage("Visible snackbar message")

    composeTestRule.setContent { GlobalSnackbarHost() }

    // Assert that the snackbar appears with the correct text.
    composeTestRule.onNodeWithText("Visible snackbar message").assertExists()
  }
}
