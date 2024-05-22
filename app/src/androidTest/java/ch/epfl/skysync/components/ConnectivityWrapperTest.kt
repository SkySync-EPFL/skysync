package ch.epfl.skysync.components

import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class ConnectivityWrapperTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testOnlineScenario() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val connectivityStatus = ContextConnectivityStatus(LocalContext.current)

      ConnectivityWrapper(connectivityStatus = connectivityStatus, navController = navController) {
        Text(text = "Online content")
      }
    }

    // Verify that the online content is displayed
    composeTestRule.onNodeWithText("Online content").assertExists()
  }

  @Test
  fun testOfflineScenario_ShowAlertDialog() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val connectivityStatus = DummyConnectivityStatus(false)

      ConnectivityWrapper(connectivityStatus = connectivityStatus, navController = navController) {
        Text(text = "Online content")
      }
    }

    // Verify that the AlertDialog is displayed
    composeTestRule.onNodeWithText("No Internet Connection").assertExists()
    composeTestRule.onNodeWithText("Feature not available offline").assertExists()
    composeTestRule.onNodeWithText("OK").assertExists()
  }

  @Test
  fun testOfflineScenario_ShowOfflineMessage() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val connectivityStatus = DummyConnectivityStatus(false)

      ConnectivityWrapper(connectivityStatus = connectivityStatus, navController = navController) {
        Text(text = "Online content")
      }
    }

    // Click the OK button in the AlertDialog
    composeTestRule.onNodeWithText("OK").performClick()

    // Verify that the offline message is displayed
    composeTestRule.onNodeWithText("Feature not available offline").assertExists()
  }
}
