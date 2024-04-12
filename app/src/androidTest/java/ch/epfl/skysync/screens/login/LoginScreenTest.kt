import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.MainActivity
import ch.epfl.skysync.screens.login.LoginScreenNodes
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/*
@RunWith(AndroidJUnit4::class)
class LoginScreenTest : TestCase() {
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()
  // The IntentsTestRule simply calls Intents.init() before the @Test block
  // and Intents.release() after the @Test block is completed. IntentsTestRule
  // is deprecated, but it was MUCH faster than using IntentsRule in our tests
  @get:Rule val intentsTestRule = IntentsTestRule(MainActivity::class.java)

  @Test
  fun buttonIsCorrectlyDisplayed() {
    ComposeScreen.onComposeScreen<LoginScreenNodes>(composeTestRule) {
      loginButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  @Test
  fun googleSignInReturnsValidActivityResult() {
    ComposeScreen.onComposeScreen<LoginScreenNodes>(composeTestRule) {
      loginButton {
        assertIsDisplayed()
        performClick()
      }
      // assert that an Intent resolving to Google Mobile Services has been sent (for sign-in)
      Intents.intended(IntentMatchers.toPackage("com.google.android.gms"))
    }
  }
}
*/