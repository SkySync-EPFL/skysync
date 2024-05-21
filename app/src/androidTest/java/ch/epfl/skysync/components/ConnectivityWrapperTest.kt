package ch.epfl.skysync.components

import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class ConnectivityWrapperTest {

  @get:Rule val composeTestRule = createComposeRule()

  /*@Before
  fun setUp() {
      setAirplaneMode(false)
  }

  @After
  fun tearDown() {
      setAirplaneMode(false)
  }*/

  @Test
  fun testOnlineScenario() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val connectivityStatus = ConnectivityStatus(LocalContext.current)

      ConnectivityWrapper(connectivityStatus = connectivityStatus, navController = navController) {
        Text(text = "Online content")
      }
    }

    // Verify that the online content is displayed
    composeTestRule.onNodeWithText("Online content").assertExists()
  }

  /*@Test
  fun testOfflineScenario_ShowAlertDialog() {
      setAirplaneMode(true)

      composeTestRule.setContent {
          val navController = rememberNavController()
          val connectivityStatus = ConnectivityStatus(LocalContext.current)

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
      setAirplaneMode(true)

      composeTestRule.setContent {
          val navController = rememberNavController()
          val connectivityStatus = ConnectivityStatus(LocalContext.current)

          ConnectivityWrapper(connectivityStatus = connectivityStatus, navController = navController) {
              Text(text = "Online content")
          }
      }

      // Click the OK button in the AlertDialog
      composeTestRule.onNodeWithText("OK").performClick()

      // Verify that the offline message is displayed
      composeTestRule.onNodeWithText("Feature not available offline").assertExists()
  }

  fun setAirplaneMode(enabled: Boolean) {
      val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          // For Android Q and above, use Settings panel
          device.executeShellCommand("settings put global airplane_mode_on ${if (enabled) 1 else 0}")
          device.executeShellCommand("am broadcast -a android.intent.action.AIRPLANE_MODE --ez state $enabled")
      } else {
          // For Android Pie and below
          device.pressHome()
          device.wait(Until.hasObject(By.desc("Apps")), 3000)
          val appsButton = device.findObject(UiSelector().description("Apps"))
          appsButton.click()

          device.wait(Until.hasObject(By.text("Settings")), 3000)
          val settingsApp = device.findObject(UiSelector().text("Settings"))
          settingsApp.click()

          device.wait(Until.hasObject(By.text("Network & internet")), 3000)
          val networkAndInternet = device.findObject(UiSelector().text("Network & internet"))
          networkAndInternet.click()

          device.wait(Until.hasObject(By.text("Airplane mode")), 3000)
          val airplaneMode = device.findObject(UiSelector().text("Airplane mode"))
          if (airplaneMode != null && airplaneMode.isChecked != enabled) {
              airplaneMode.click()
          }
      }

      // Wait for the settings change to take effect
      Thread.sleep(1000)
  }*/
}
