package ch.epfl.skysync.viewmodel

import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TimerViewModelTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    lateinit var timerViewModel: TimerViewModel

    @Before
    fun setUp() {
        composeTestRule.setContent {
            timerViewModel = TimerViewModel.createViewModel()
            val countString by timerViewModel.counter.collectAsStateWithLifecycle()
            Text(countString)
        }
    }


    @Test
    fun isZeroBeforeStart() = runTest {
            val countString = timerViewModel.counter.value
            assertTrue(countString == "00:00:00")
        }


    @Test
    fun testStartFunction() = runTest {
        timerViewModel.start()
        delay(2000) // wait for 2 seconds
        val isRunning = timerViewModel.isRunning.value
        val countString = timerViewModel.counter.value
        assertTrue(isRunning)
        assertNotEquals("00:00:00", countString) // counter should have increased
    }

    @Test
    fun testStopFunction() = runTest {
        timerViewModel.start()
        delay(2000) // wait for 2 seconds
        timerViewModel.stop()
        val isRunning = timerViewModel.isRunning.value
        val countString1 = timerViewModel.counter.value
        delay(2000) // wait for another 2 seconds
        val countString2 = timerViewModel.counter.value
        assertFalse(isRunning)
        assertEquals(countString1, countString2) // counter should not have increased
    }
}

fun convertToMilliseconds(time: String): Long {
    val parts = time.split(":").map { it.toLong() }
    val hours = parts[0]
    val minutes = parts[1]
    val seconds = parts[2]
    return (hours * 3600 + minutes * 60 + seconds) * 1000
}