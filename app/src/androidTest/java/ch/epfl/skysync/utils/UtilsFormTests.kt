package ch.epfl.skysync.utils

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick

/**
 * Input a time in the time picker
 * @param composeTestRule the testing rule
 * @param hour the hour to input
 * @param minute the minute to input (it will be rounded to a multiple of 5)
 */
@OptIn(ExperimentalTestApi::class)
fun inputTimePicker(
    composeTestRule : ComposeTestRule,
    hour : Int,
    minute : Int,
){
    composeTestRule.waitUntilExactlyOneExists(hasTestTag("Time Picker"))
    composeTestRule
        .onNodeWithTag("Time Picker")
        .onChild()
        .onChildAt(4)
        .onChildAt(hour)
        .performClick()
    composeTestRule
        .onNodeWithTag("Time Picker")
        .onChild()
        .onChildAt(4)
        .onChildAt(minute/5)
        .performClick()
    composeTestRule
        .onNodeWithText("Confirm")
        .performClick()
}