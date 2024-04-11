package ch.epfl.skysync.screens.chat

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.screens.ChatScreen
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.Before
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class ChatScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    lateinit var navController: TestNavHostController

    @Before
    fun testSetup() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            ChatScreen(navController = navController)
        }
    }

    @Test
    fun verifyChatScreenIsDisplayed() {

        composeTestRule.onNode(hasTestTag("ChatScreenScaffold")).assertIsDisplayed()
        composeTestRule.onNode(hasTestTag("Chat")).assertIsDisplayed()
    }







}
